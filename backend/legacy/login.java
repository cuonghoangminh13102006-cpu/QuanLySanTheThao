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
 * Backend xử lý Đăng nhập & Đăng ký cho hệ thống Quản lý sân thể thao.
 * TỰ ĐỘNG THÔNG MINH:
 * - Nếu có kết nối SQL Server: Tự động lưu và truy vấn từ SQL Server.
 * - Nếu máy chưa bật SQL Server: Tự động chạy với bộ nhớ dữ liệu mẫu (In-Memory Database)
 *   để bạn có thể trải nghiệm và kiểm thử ngay lập tức mà không gặp bất kỳ lỗi nào!
 */
public class login {

    // =========================================================================
    // 1. CẤU HÌNH KẾT NỐI SQL SERVER (Tùy chọn)
    // =========================================================================
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=quan_ly_san_the_thao;encrypt=true;trustServerCertificate=true;";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "YourPassword123";

    private static boolean isSqlServerConnected = false;

    // Danh sách dữ liệu tài khoản có sẵn (từ seeddata.sql)
    private static final List<TaiKhoan> danhSachTaiKhoan = new ArrayList<>();

    static {
        // Nạp sẵn các tài khoản mẫu để bạn test ngay
        danhSachTaiKhoan.add(new TaiKhoan(1, "Quản trị viên", "0901234567", "admin", "1234", "Admin"));
        danhSachTaiKhoan.add(new TaiKhoan(2, "Nguyễn Văn Chủ", "0912345678", "chusan@gmail.com", "123456", "Chủ sân"));
        danhSachTaiKhoan.add(new TaiKhoan(3, "Trần Văn Khách", "0923456789", "khachhang1@gmail.com", "123456", "Khách hàng"));
        danhSachTaiKhoan.add(new TaiKhoan(4, "Lê Thị Hoa", "0934567890", "khachhang2@gmail.com", "123456", "Khách hàng"));
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {}
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void kiemTraKetNoiDatabase() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                isSqlServerConnected = true;
                System.out.println("[DATABASE] ĐÃ KẾT NỐI MICROSOFT SQL SERVER THÀNH CÔNG!");
            }
        } catch (Exception e) {
            isSqlServerConnected = false;
            System.out.println("[DATABASE] Chưa bật SQL Server -> Đang chạy ở chế độ Sẵn sàng (Dữ liệu mẫu từ seeddata.sql).");
        }
    }

    // =========================================================================
    // 2. MODEL TAIKHOAN & PHẢN HỒI
    // =========================================================================
    public static class TaiKhoan {
        private int maTaiKhoan;
        private String hoVaTen;
        private String soDienThoai;
        private String email;
        private String matKhau;
        private String vaiTro;

        public TaiKhoan(int maTaiKhoan, String hoVaTen, String soDienThoai, String email, String matKhau, String vaiTro) {
            this.maTaiKhoan = maTaiKhoan;
            this.hoVaTen = hoVaTen;
            this.soDienThoai = soDienThoai;
            this.email = email;
            this.matKhau = matKhau;
            this.vaiTro = vaiTro;
        }

        public int getMaTaiKhoan() { return maTaiKhoan; }
        public String getHoVaTen() { return hoVaTen; }
        public String getSoDienThoai() { return soDienThoai; }
        public String getEmail() { return email; }
        public String getMatKhau() { return matKhau; }
        public String getVaiTro() { return vaiTro; }
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
    // 3. KIỂM TRA ĐỊNH DẠNG (VALIDATION)
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
        if (trimmed.equalsIgnoreCase("admin")) return true;
        return EMAIL_PATTERN.matcher(trimmed).matches();
    }

    // =========================================================================
    // 4. XỬ LÝ ĐĂNG NHẬP
    // =========================================================================
    public static KetQuaXuLy xuLyDangNhap(String identifier, String matKhau, String phuongThuc) {
        if (identifier == null) identifier = "";
        if (matKhau == null) matKhau = "";
        identifier = identifier.trim();

        // Kiểm tra định dạng theo luồng rẽ nhánh
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

        // Trường hợp 1: Nếu đã kết nối SQL Server
        if (isSqlServerConnected) {
            try (Connection conn = getConnection()) {
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
                                    null,
                                    rs.getString("VaiTro")
                                );
                                System.out.println("-> [ĐĂNG NHẬP THÀNH CÔNG] " + identifier + " (" + tk.getVaiTro() + ")");
                                return new KetQuaXuLy(true, "Đăng nhập thành công", tk);
                            }
                        }
                    }
                }
                System.out.println("-> [ĐĂNG NHẬP THẤT BẠI] Sai tài khoản hoặc mật khẩu: " + identifier);
                return new KetQuaXuLy(false, "Đăng nhập thất bại, vui lòng thử lại", null);
            } catch (SQLException e) {
                return new KetQuaXuLy(false, "Lỗi kết nối hệ thống", null);
            }
        }

        // Trường hợp 2: Chế độ Sẵn sàng (Đối chiếu dữ liệu mẫu)
        for (TaiKhoan tk : danhSachTaiKhoan) {
            boolean khopDinhDanh = identifier.equalsIgnoreCase(tk.getEmail()) || identifier.equals(tk.getSoDienThoai());
            boolean khopMatKhau = matKhau.equals(tk.getMatKhau());
            if (khopDinhDanh && khopMatKhau) {
                TaiKhoan userSafe = new TaiKhoan(tk.getMaTaiKhoan(), tk.getHoVaTen(), tk.getSoDienThoai(), tk.getEmail(), null, tk.getVaiTro());
                System.out.println("-> [ĐĂNG NHẬP THÀNH CÔNG] " + identifier + " (" + tk.getVaiTro() + ")");
                return new KetQuaXuLy(true, "Đăng nhập thành công", userSafe);
            }
        }
        System.out.println("-> [ĐĂNG NHẬP THẤT BẠI] Sai tài khoản hoặc mật khẩu: " + identifier);
        return new KetQuaXuLy(false, "Đăng nhập thất bại, vui lòng thử lại", null);
    }

    // =========================================================================
    // 5. XỬ LÝ ĐĂNG KÝ
    // =========================================================================
    public static KetQuaXuLy xuLyDangKy(String hoVaTen, String soDienThoai, String email, String matKhau, String xacNhanMatKhau) {
        // Kiểm tra các luồng rẽ nhánh validation
        if (hoVaTen == null || hoVaTen.trim().isEmpty()) {
            return new KetQuaXuLy(false, "Nhập họ và tên", null);
        }
        if (!kiemTraSoDienThoai(soDienThoai)) {
            return new KetQuaXuLy(false, "Số điện thoại di động không đúng", null);
        }
        if (!kiemTraEmail(email)) {
            return new KetQuaXuLy(false, "Email không hợp lệ, vui lòng nhập lại", null);
        }
        if (matKhau == null || matKhau.length() < 8) {
            return new KetQuaXuLy(false, "Mật khẩu có độ dài tối thiểu 8 ký tự", null);
        }
        if (xacNhanMatKhau != null && !matKhau.equals(xacNhanMatKhau)) {
            return new KetQuaXuLy(false, "Xác nhận mật khẩu không khớp", null);
        }

        hoVaTen = hoVaTen.trim();
        soDienThoai = soDienThoai.trim();
        email = email.trim();

        // Trường hợp 1: Nếu có kết nối SQL Server
        if (isSqlServerConnected) {
            try (Connection conn = getConnection()) {
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

                String insertSql = "INSERT INTO TAIKHOAN (HoVaTen, SoDienThoai, Email, MatKhau, VaiTro) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setNString(1, hoVaTen);
                    insertStmt.setString(2, soDienThoai);
                    insertStmt.setString(3, email);
                    insertStmt.setString(4, matKhau);
                    insertStmt.setNString(5, "Khách hàng");
                    insertStmt.executeUpdate();
                }

                System.out.println(">>> [ĐĂNG KÝ MỚI] Đã lưu vào SQL Server: " + email + " (" + hoVaTen + ")");
                return new KetQuaXuLy(true, "Đăng ký thành công", null);
            } catch (SQLException e) {
                return new KetQuaXuLy(false, "Lỗi kết nối hệ thống", null);
            }
        }

        // Trường hợp 2: Chế độ Sẵn sàng (Lưu vào danh sách dữ liệu)
        for (TaiKhoan tk : danhSachTaiKhoan) {
            if (tk.getEmail().equalsIgnoreCase(email) || tk.getSoDienThoai().equals(soDienThoai)) {
                return new KetQuaXuLy(false, "Số điện thoại hoặc Email đã tồn tại trong hệ thống", null);
            }
        }

        int newId = danhSachTaiKhoan.size() + 1;
        TaiKhoan tkMoi = new TaiKhoan(newId, hoVaTen, soDienThoai, email, matKhau, "Khách hàng");
        danhSachTaiKhoan.add(tkMoi);

        System.out.println(">>> [ĐĂNG KÝ MỚI] Đã tạo tài khoản: " + email + " (" + hoVaTen + ")");
        return new KetQuaXuLy(true, "Đăng ký thành công", null);
    }

    // =========================================================================
    // 6. KHỞI CHẠY MÁY CHỦ API CHO WEB (login.html)
    // =========================================================================
    public static void startHttpServer(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // POST /api/login
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

            // POST /api/register
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

            server.setExecutor(null);
            server.start();
            System.out.println("=================================================================");
            System.out.println(" MÁY CHỦ BACKEND ĐANG CHẠY TẠI: http://localhost:" + port);
            System.out.println(" -> Hãy mở file frontend/login.html trên trình duyệt để sử dụng!");
            System.out.println("=================================================================");

        } catch (IOException e) {
            System.err.println("Không thể mở cổng " + port + ": " + e.getMessage());
        }
    }

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
        if (m.find()) return m.group(1);
        return "";
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // =========================================================================
    // 7. HÀM MAIN
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("    QUẢN LÝ SÂN THỂ THAO - BACKEND JAVA (login.java)");
        System.out.println("-----------------------------------------------------------------");

        // 1. Kiểm tra database
        kiemTraKetNoiDatabase();

        // 2. Khởi động server
        startHttpServer(8080);
    }
}
