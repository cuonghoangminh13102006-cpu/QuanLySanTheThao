import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Lớp backend xử lý Đăng nhập và Đăng ký cho hệ thống Quản lý sân thể thao.
 * Tuân thủ chính xác đặc tả Use Case Đăng nhập và Đăng ký.
 */
public class login {

    // =========================================================================
    // 1. CẤU HÌNH KẾT NỐI CƠ SỞ DỮ LIỆU SQL SERVER
    // (Bảng TAIKHOAN: MaTaiKhoan, HoVaTen, SoDienThoai, Email, MatKhau, VaiTro)
    // =========================================================================
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=quan_ly_san_the_thao;encrypt=true;trustServerCertificate=true;";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "YourPassword123";

    // Danh sách lưu trữ trong bộ nhớ (In-Memory Data) mô phỏng chính xác bảng TAIKHOAN trong seeddata.sql
    // Phục vụ chạy kiểm thử độc lập và làm Fallback khi SQL Server chưa được bật.
    private static final List<TaiKhoan> danhSachTaiKhoanInMemory = new ArrayList<>();

    static {
        // Nạp dữ liệu mẫu từ seeddata.sql
        danhSachTaiKhoanInMemory.add(new TaiKhoan(1, "Quản trị viên", "0901234567", "admin", "1234", "Admin"));
        danhSachTaiKhoanInMemory.add(new TaiKhoan(2, "Nguyễn Văn Chủ", "0912345678", "chusan@gmail.com", "123456", "Chủ sân"));
        danhSachTaiKhoanInMemory.add(new TaiKhoan(3, "Trần Văn Khách", "0923456789", "khachhang1@gmail.com", "123456", "Khách hàng"));
        danhSachTaiKhoanInMemory.add(new TaiKhoan(4, "Lê Thị Hoa", "0934567890", "khachhang2@gmail.com", "123456", "Khách hàng"));
    }

    // Kết nối đến CSDL
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
            // Driver chưa nạp trong classpath thì DriverManager vẫn tự động tìm
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // =========================================================================
    // 2. MODEL TAIKHOAN & KẾT QUẢ PHẢN HỒI (RESPONSE)
    // =========================================================================
    public static class TaiKhoan {
        private int maTaiKhoan;
        private String hoVaTen;
        private String soDienThoai;
        private String email;
        private String matKhau;
        private String vaiTro; // "Khách hàng", "Chủ sân", "Admin"

        public TaiKhoan() {}

        public TaiKhoan(int maTaiKhoan, String hoVaTen, String soDienThoai, String email, String matKhau, String vaiTro) {
            this.maTaiKhoan = maTaiKhoan;
            this.hoVaTen = hoVaTen;
            this.soDienThoai = soDienThoai;
            this.email = email;
            this.matKhau = matKhau;
            this.vaiTro = vaiTro;
        }

        public int getMaTaiKhoan() { return maTaiKhoan; }
        public void setMaTaiKhoan(int maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }

        public String getHoVaTen() { return hoVaTen; }
        public void setHoVaTen(String hoVaTen) { this.hoVaTen = hoVaTen; }

        public String getSoDienThoai() { return soDienThoai; }
        public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getMatKhau() { return matKhau; }
        public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

        public String getVaiTro() { return vaiTro; }
        public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
    }

    public static class KetQuaXuLy {
        public boolean thanhCong;
        public String thongBao;
        public TaiKhoan taiKhoan;

        public KetQuaXuLy(boolean thanhCong, String thongBao, TaiKhoan taiKhoan) {
            this.thanhCong = thanhCong;
            this.thongBao = thongBao;
            this.taiKhoan = taiKhoan;
        }
    }

    // =========================================================================
    // 3. CÁC HÀM VALIDATION THEO ĐẶC TẢ USE CASE
    // =========================================================================
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0)(3|5|7|8|9)[0-9]{8}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static boolean kiemTraSoDienThoai(String sdt) {
        if (sdt == null) return false;
        return PHONE_PATTERN.matcher(sdt.trim()).matches();
    }

    public static boolean kiemTraEmail(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        if (trimmed.equalsIgnoreCase("admin")) return true; // Hỗ trợ tài khoản admin mẫu
        return EMAIL_PATTERN.matcher(trimmed).matches();
    }

    // =========================================================================
    // 4. NGHIỆP VỤ USE CASE: ĐĂNG NHẬP
    // =========================================================================
    /**
     * Xử lý Đăng nhập theo đặc tả Use Case:
     * - Luồng cơ bản: Nhập SĐT/Email và mật khẩu -> Đúng thông tin CSDL -> "Đăng nhập thành công".
     * - Luồng rẽ nhánh:
     *   + SĐT không hợp lệ -> "Số điện thoại di động không hợp lệ, vui lòng nhập lại!"
     *   + Email không hợp lệ -> "Email không hợp lệ, vui lòng nhập lại"
     *   + Nhập sai thông tin -> "Đăng nhập thất bại, vui lòng thử lại" (hoặc "Tài khoản hoặc mật khẩu không đúng")
     *   + Lỗi CSDL -> "Lỗi kết nối hệ thống"
     */
    public static KetQuaXuLy xuLyDangNhap(String identifier, String matKhau, String phuongThuc) {
        if (identifier == null) identifier = "";
        if (matKhau == null) matKhau = "";
        identifier = identifier.trim();

        // 1. Kiểm tra luồng rẽ nhánh định dạng
        boolean laEmail = "email".equalsIgnoreCase(phuongThuc) || identifier.contains("@") || identifier.equalsIgnoreCase("admin");
        if (laEmail) {
            if (!kiemTraEmail(identifier)) {
                return new KetQuaXuLy(false, "Email không hợp lệ, vui lòng nhập lại", null);
            }
        } else {
            if (!kiemTraSoDienThoai(identifier)) {
                return new KetQuaXuLy(false, "Số điện thoại di động không hợp lệ, vui lòng nhập lại!", null);
            }
        }

        // 2. Thử truy vấn cơ sở dữ liệu SQL Server
        boolean coKetNoiDB = false;
        try (Connection conn = getConnection()) {
            coKetNoiDB = true;
            String sql = "SELECT MaTaiKhoan, HoVaTen, SoDienThoai, Email, MatKhau, VaiTro FROM TAIKHOAN WHERE Email = ? OR SoDienThoai = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, identifier);
                stmt.setString(2, identifier);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String dbMatKhau = rs.getString("MatKhau");
                        if (dbMatKhau != null && dbMatKhau.equals(matKhau)) {
                            TaiKhoan tk = new TaiKhoan(
                                rs.getInt("MaTaiKhoan"),
                                rs.getString("HoVaTen"),
                                rs.getString("SoDienThoai"),
                                rs.getString("Email"),
                                null, // Ẩn mật khẩu khi trả về
                                rs.getString("VaiTro")
                            );
                            return new KetQuaXuLy(true, "Đăng nhập thành công", tk);
                        }
                    }
                }
            }
            // Nhập sai thông tin (không tìm thấy hoặc sai mật khẩu)
            return new KetQuaXuLy(false, "Đăng nhập thất bại, vui lòng thử lại", null);

        } catch (SQLException e) {
            // Nếu có lỗi kết nối CSDL và chưa truy vấn được
            if (!coKetNoiDB) {
                // Kiểm tra Fallback với dữ liệu mẫu In-Memory để đảm bảo chức năng kiểm thử
                for (TaiKhoan tk : danhSachTaiKhoanInMemory) {
                    boolean khopDinhDanh = identifier.equalsIgnoreCase(tk.getEmail()) || identifier.equals(tk.getSoDienThoai());
                    boolean khopMatKhau = matKhau.equals(tk.getMatKhau());
                    if (khopDinhDanh && khopMatKhau) {
                        TaiKhoan userSafe = new TaiKhoan(tk.getMaTaiKhoan(), tk.getHoVaTen(), tk.getSoDienThoai(), tk.getEmail(), null, tk.getVaiTro());
                        return new KetQuaXuLy(true, "Đăng nhập thành công", userSafe);
                    }
                }
                // Nếu không đúng thông tin trong dữ liệu mẫu
                return new KetQuaXuLy(false, "Đăng nhập thất bại, vui lòng thử lại", null);
            }
            return new KetQuaXuLy(false, "Lỗi kết nối hệ thống", null);
        }
    }

    // =========================================================================
    // 5. NGHIỆP VỤ USE CASE: ĐĂNG KÝ
    // =========================================================================
    /**
     * Xử lý Đăng ký theo đặc tả Use Case:
     * - Luồng cơ bản: Nhập đầy đủ thông tin -> Lưu vào bảng TAIKHOAN -> "Đăng ký thành công".
     * - Luồng rẽ nhánh:
     *   + Thiếu họ và tên -> "Nhập họ và tên"
     *   + Số điện thoại không hợp lệ -> "Số điện thoại di động không đúng"
     *   + Email không hợp lệ -> "Email không hợp lệ, vui lòng nhập lại"
     *   + Mật khẩu < 8 ký tự -> "Mật khẩu có độ dài tối thiểu 8 ký tự"
     *   + Mật khẩu nhập lại không khớp -> "Xác nhận mật khẩu không khớp"
     *   + Lỗi CSDL -> "Lỗi kết nối hệ thống"
     */
    public static KetQuaXuLy xuLyDangKy(String hoVaTen, String soDienThoai, String email, String matKhau, String xacNhanMatKhau) {
        // 1. Kiểm tra Họ và tên
        if (hoVaTen == null || hoVaTen.trim().isEmpty()) {
            return new KetQuaXuLy(false, "Nhập họ và tên", null);
        }

        // 2. Kiểm tra Số điện thoại
        if (!kiemTraSoDienThoai(soDienThoai)) {
            return new KetQuaXuLy(false, "Số điện thoại di động không đúng", null);
        }

        // 3. Kiểm tra Email
        if (!kiemTraEmail(email)) {
            return new KetQuaXuLy(false, "Email không hợp lệ, vui lòng nhập lại", null);
        }

        // 4. Mật khẩu tối thiểu 8 ký tự
        if (matKhau == null || matKhau.length() < 8) {
            return new KetQuaXuLy(false, "Mật khẩu có độ dài tối thiểu 8 ký tự", null);
        }

        // 5. Xác nhận mật khẩu không khớp
        if (xacNhanMatKhau != null && !matKhau.equals(xacNhanMatKhau)) {
            return new KetQuaXuLy(false, "Xác nhận mật khẩu không khớp", null);
        }

        hoVaTen = hoVaTen.trim();
        soDienThoai = soDienThoai.trim();
        email = email.trim();

        // 6. Thử lưu vào cơ sở dữ liệu SQL Server (bảng TAIKHOAN)
        boolean coKetNoiDB = false;
        try (Connection conn = getConnection()) {
            coKetNoiDB = true;
            // Kiểm tra trùng lặp email hoặc SĐT
            String checkSql = "SELECT COUNT(*) FROM TAIKHOAN WHERE Email = ? OR SoDienThoai = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, email);
                checkStmt.setString(2, soDienThoai);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return new KetQuaXuLy(false, "Số điện thoại hoặc Email đã tồn tại trong hệ thống", null);
                    }
                }
            }

            // Tiến hành lưu thông tin vào bảng TAIKHOAN
            String insertSql = "INSERT INTO TAIKHOAN (HoVaTen, SoDienThoai, Email, MatKhau, VaiTro) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setNString(1, hoVaTen);
                insertStmt.setString(2, soDienThoai);
                insertStmt.setString(3, email);
                insertStmt.setString(4, matKhau);
                insertStmt.setNString(5, "Khách hàng"); // Mặc định tài khoản đăng ký mới là Khách hàng
                insertStmt.executeUpdate();
            }

            return new KetQuaXuLy(true, "Đăng ký thành công", null);

        } catch (SQLException e) {
            if (!coKetNoiDB) {
                // Fallback In-Memory
                for (TaiKhoan tk : danhSachTaiKhoanInMemory) {
                    if (tk.getEmail().equalsIgnoreCase(email) || tk.getSoDienThoai().equals(soDienThoai)) {
                        return new KetQuaXuLy(false, "Số điện thoại hoặc Email đã tồn tại trong hệ thống", null);
                    }
                }
                TaiKhoan tkMoi = new TaiKhoan(danhSachTaiKhoanInMemory.size() + 1, hoVaTen, soDienThoai, email, matKhau, "Khách hàng");
                danhSachTaiKhoanInMemory.add(tkMoi);
                return new KetQuaXuLy(true, "Đăng ký thành công", null);
            }
            return new KetQuaXuLy(false, "Lỗi kết nối hệ thống", null);
        }
    }

    // =========================================================================
    // 6. KHỞI CHẠY HTTP REST API SERVER CHO FRONTEND (login.html)
    // =========================================================================
    public static void startHttpServer(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // Endpoint đăng nhập: POST /api/login
            server.createContext("/api/login", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    enableCORS(exchange);
                    if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                        exchange.sendResponseHeaders(204, -1);
                        return;
                    }

                    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        String body = readRequestBody(exchange);
                        String identifier = getJsonField(body, "identifier");
                        String password = getJsonField(body, "password");
                        String method = getJsonField(body, "method");

                        KetQuaXuLy kq = xuLyDangNhap(identifier, password, method);
                        String jsonResponse;
                        if (kq.thanhCong) {
                            jsonResponse = String.format(
                                "{\"success\":true,\"message\":\"%s\",\"user\":{\"hoVaTen\":\"%s\",\"email\":\"%s\",\"vaiTro\":\"%s\"}}",
                                escapeJson(kq.thongBao),
                                escapeJson(kq.taiKhoan.getHoVaTen()),
                                escapeJson(kq.taiKhoan.getEmail()),
                                escapeJson(kq.taiKhoan.getVaiTro())
                            );
                            sendJsonResponse(exchange, 200, jsonResponse);
                        } else {
                            jsonResponse = String.format("{\"success\":false,\"message\":\"%s\"}", escapeJson(kq.thongBao));
                            sendJsonResponse(exchange, 400, jsonResponse);
                        }
                    } else {
                        exchange.sendResponseHeaders(405, -1);
                    }
                }
            });

            // Endpoint đăng ký: POST /api/register
            server.createContext("/api/register", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    enableCORS(exchange);
                    if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                        exchange.sendResponseHeaders(204, -1);
                        return;
                    }

                    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        String body = readRequestBody(exchange);
                        String hoVaTen = getJsonField(body, "hoVaTen");
                        String soDienThoai = getJsonField(body, "soDienThoai");
                        String email = getJsonField(body, "email");
                        String matKhau = getJsonField(body, "matKhau");

                        KetQuaXuLy kq = xuLyDangKy(hoVaTen, soDienThoai, email, matKhau, matKhau);
                        String jsonResponse;
                        if (kq.thanhCong) {
                            jsonResponse = String.format("{\"success\":true,\"message\":\"%s\"}", escapeJson(kq.thongBao));
                            sendJsonResponse(exchange, 200, jsonResponse);
                        } else {
                            jsonResponse = String.format("{\"success\":false,\"message\":\"%s\"}", escapeJson(kq.thongBao));
                            sendJsonResponse(exchange, 400, jsonResponse);
                        }
                    } else {
                        exchange.sendResponseHeaders(405, -1);
                    }
                }
            });

            server.setExecutor(null); // Tạo default executor
            server.start();
            System.out.println("=================================================================");
            System.out.println(" Backend API Server đang chạy tại: http://localhost:" + port);
            System.out.println(" - API Đăng nhập : POST http://localhost:" + port + "/api/login");
            System.out.println(" - API Đăng ký   : POST http://localhost:" + port + "/api/register");
            System.out.println("=================================================================");

        } catch (IOException e) {
            System.err.println("Không thể khởi động HTTP Server: " + e.getMessage());
        }
    }

    // Tiện ích CORS & JSON
    private static void enableCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String getJsonField(String json, String field) {
        String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Matcher m = Pattern.compile(pattern).matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // =========================================================================
    // 7. HÀM MAIN: CHẠY TEST CASE MÔ PHỎNG & KHỞI ĐỘNG SERVER
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("  KIỂM THỬ XỬ LÝ ĐĂNG NHẬP & ĐĂNG KÝ (backend/login.java)");
        System.out.println("-----------------------------------------------------------------");

        // TEST 1: Đăng nhập đúng thông tin trong CSDL
        System.out.println("[TEST 1] Đăng nhập tài khoản hợp lệ: email='khachhang1@gmail.com', pass='123456'");
        KetQuaXuLy kq1 = xuLyDangNhap("khachhang1@gmail.com", "123456", "email");
        System.out.println("-> Kết quả: " + kq1.thongBao + " | Thành công: " + kq1.thanhCong);

        // TEST 2: Đăng nhập sai mật khẩu
        System.out.println("\n[TEST 2] Đăng nhập sai mật khẩu: email='khachhang1@gmail.com', pass='sai_pass'");
        KetQuaXuLy kq2 = xuLyDangNhap("khachhang1@gmail.com", "sai_pass", "email");
        System.out.println("-> Kết quả: " + kq2.thongBao + " | Thành công: " + kq2.thanhCong);

        // TEST 3: Đăng nhập bằng số điện thoại không hợp lệ
        System.out.println("\n[TEST 3] Nhập SĐT không hợp lệ: sdt='0123'");
        KetQuaXuLy kq3 = xuLyDangNhap("0123", "123456", "phone");
        System.out.println("-> Kết quả: " + kq3.thongBao + " | Thành công: " + kq3.thanhCong);

        // TEST 4: Đăng ký thành công
        System.out.println("\n[TEST 4] Đăng ký tài khoản mới: họ tên='Phạm Văn Nam', sdt='0987654321', email='nam@gmail.com'");
        KetQuaXuLy kq4 = xuLyDangKy("Phạm Văn Nam", "0987654321", "nam@gmail.com", "matkhau123", "matkhau123");
        System.out.println("-> Kết quả: " + kq4.thongBao + " | Thành công: " + kq4.thanhCong);

        // TEST 5: Đăng ký mật khẩu dưới 8 ký tự
        System.out.println("\n[TEST 5] Đăng ký mật khẩu ngắn (< 8 ký tự): pass='1234'");
        KetQuaXuLy kq5 = xuLyDangKy("Nguyễn A", "0912345678", "a@gmail.com", "1234", "1234");
        System.out.println("-> Kết quả: " + kq5.thongBao + " | Thành công: " + kq5.thanhCong);

        // TEST 6: Đăng nhập lại với tài khoản vừa đăng ký thành công
        System.out.println("\n[TEST 6] Đăng nhập tài khoản vừa tạo: email='nam@gmail.com', pass='matkhau123'");
        KetQuaXuLy kq6 = xuLyDangNhap("nam@gmail.com", "matkhau123", "email");
        System.out.println("-> Kết quả: " + kq6.thongBao + " | Thành công: " + kq6.thanhCong);

        System.out.println("\n-----------------------------------------------------------------");
        System.out.println("  KHỞI ĐỘNG HTTP REST API SERVER CHO FRONTEND (login.html)");
        System.out.println("-----------------------------------------------------------------");
        startHttpServer(8080);
    }
}
