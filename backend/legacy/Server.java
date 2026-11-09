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
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * HỆ THỐNG QUẢN LÝ SÂN THỂ THAO - BACKEND API SERVER TỔNG THỂ
 * Kiến trúc Modular Monolith đáp ứng trọn vẹn 12 USE CASE:
 * -----------------------------------------------------------------------------
 * 1. Module Identity  : UC 1 (Đăng ký), UC 2 (Đăng nhập), UC 12 (Tài khoản cá nhân)
 * 2. Module Catalog   : UC 8 (Tìm kiếm và xem sân)
 * 3. Module Booking   : UC 9 (Đặt sân), UC 10 (Theo dõi đơn), UC 11 (Yêu cầu hủy)
 * 4. Module Owner     : UC 3 (Quản lý sân), UC 5 (Quản lý lịch & đơn đặt)
 * 5. Module Payment   : UC 6 (Quản lý thanh toán & báo cáo doanh thu)
 * 6. Module Admin     : UC 4 (Quản lý tài khoản), UC 7 (Giám sát sân & đơn đặt)
 * -----------------------------------------------------------------------------
 */
public class Server {

    public static final int PORT = 8080;

    // Cấu hình Microsoft SQL Server (database quan_ly_san_the_thao)
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=quan_ly_san_the_thao;encrypt=true;trustServerCertificate=true;";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "YourPassword123";

    private static boolean isSqlServerAvailable = false;

    public static Connection getDbConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {}
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // =========================================================================
    // DỮ LIỆU IN-MEMORY MẪU (Đúng 100% theo schema.sql và seeddata.sql)
    // Phục vụ chạy ngay khi chưa bật SQL Server
    // =========================================================================
    public static class Account {
        public String id, name, phone, email, password, role, status;
        public Account(String id, String name, String phone, String email, String password, String role, String status) {
            this.id = id; this.name = name; this.phone = phone; this.email = email;
            this.password = password; this.role = role; this.status = status;
        }
    }

    public static class Court {
        public String id, name, facility, address, type, open, close, operational, ownerId, ownerName, description;
        public double price;
        public boolean visible;
        public Court(String id, String name, String facility, String address, String type, double price, String open, String close, String operational, boolean visible, String ownerId, String ownerName, String description) {
            this.id = id; this.name = name; this.facility = facility; this.address = address; this.type = type;
            this.price = price; this.open = open; this.close = close; this.operational = operational;
            this.visible = visible; this.ownerId = ownerId; this.ownerName = ownerName; this.description = description;
        }
    }

    public static class BookingOrder {
        public String id, accountId, customerName, courtId, date, start, end, status, reason;
        public double rent, service, deposit;
        public BookingOrder(String id, String accountId, String customerName, String courtId, String date, String start, String end, double rent, double service, double deposit, String status, String reason) {
            this.id = id; this.accountId = accountId; this.customerName = customerName; this.courtId = courtId;
            this.date = date; this.start = start; this.end = end; this.rent = rent;
            this.service = service; this.deposit = deposit; this.status = status; this.reason = reason;
        }
    }

    public static class TransactionLog {
        public String id, bookingId, type, method, date, by;
        public double amount;
        public TransactionLog(String id, String bookingId, String type, double amount, String method, String date, String by) {
            this.id = id; this.bookingId = bookingId; this.type = type; this.amount = amount;
            this.method = method; this.date = date; this.by = by;
        }
    }

    public static final List<Account> accounts = Collections.synchronizedList(new ArrayList<>());
    public static final List<Court> courts = Collections.synchronizedList(new ArrayList<>());
    public static final List<BookingOrder> bookings = Collections.synchronizedList(new ArrayList<>());
    public static final List<TransactionLog> transactions = Collections.synchronizedList(new ArrayList<>());

    static {
        // Nạp dữ liệu mẫu
        accounts.add(new Account("TK001", "Quản trị viên", "0901234567", "admin", "1234", "Admin", "Hoạt động"));
        accounts.add(new Account("TK002", "Nguyễn Hoàng Anh", "0912345678", "chusan@gmail.com", "123456", "Chủ sân", "Hoạt động"));
        accounts.add(new Account("TK003", "Trần Văn Khách", "0923456789", "khachhang1@gmail.com", "123456", "Khách hàng", "Hoạt động"));
        accounts.add(new Account("TK004", "Lê Thị Hoa", "0934567890", "khachhang2@gmail.com", "123456", "Khách hàng", "Hoạt động"));

        courts.add(new Court("S001", "Sân bóng đá 7 người A", "Cơ sở Thảo Điền", "32 Quốc Hương, TP. Thủ Đức", "Bóng đá 7 người", 520000, "06:00", "22:00", "Đang hoạt động", true, "TK002", "Nguyễn Hoàng Anh", "Sân cỏ nhân tạo FIFA, đèn LED"));
        courts.add(new Court("S002", "Sân cầu lông B2", "Cơ sở Thảo Điền", "32 Quốc Hương, TP. Thủ Đức", "Cầu lông", 150000, "06:00", "22:00", "Đang hoạt động", true, "TK002", "Nguyễn Hoàng Anh", "Sân thảm PVC chống trơn"));
        courts.add(new Court("S003", "Sân bóng rổ C1", "Cơ sở Phú Nhuận", "18 Hoa Sứ, Phú Nhuận", "Bóng rổ", 260000, "06:00", "22:00", "Đang hoạt động", true, "TK002", "Nguyễn Hoàng Anh", "Sàn gỗ trong nhà"));
        courts.add(new Court("S004", "Sân bóng đá 5 người D1", "Cơ sở Bình Thạnh", "45 Điện Biên Phủ, Bình Thạnh", "Bóng đá 5 người", 340000, "06:00", "22:00", "Đang hoạt động", false, "TK002", "Nguyễn Hoàng Anh", "Sân cỏ mới"));

        bookings.add(new BookingOrder("D001", "TK003", "Trần Văn Khách", "S001", "2026-11-10", "18:00", "19:00", 520000, 0, 100000, "Chờ xử lý", ""));
        bookings.add(new BookingOrder("D002", "TK004", "Lê Thị Hoa", "S001", "2026-11-10", "19:00", "20:00", 520000, 40000, 200000, "Đã xác nhận", ""));
        bookings.add(new BookingOrder("D003", "TK003", "Trần Văn Khách", "S002", "2026-11-10", "17:00", "18:00", 150000, 0, 0, "Đã xác nhận", ""));

        transactions.add(new TransactionLog("G001", "D001", "Thu tiền", 100000, "Chuyển khoản QR", "2026-11-10", "Nguyễn Hoàng Anh"));
        transactions.add(new TransactionLog("G002", "D002", "Thu tiền", 200000, "Chuyển khoản QR", "2026-11-10", "Nguyễn Hoàng Anh"));
    }

    // =========================================================================
    // KHỞI TẠO HTTP SERVER VÀ GẮN ROUTE CÁC MODULE
    // =========================================================================
    public static void main(String[] args) throws IOException {
        System.out.println("=================================================================");
        System.out.println("   KHỞI ĐỘNG HỆ THỐNG BACKEND QUẢN LÝ SÂN THỂ THAO (PORT " + PORT + ")");
        System.out.println("=================================================================");

        // Thử kết nối SQL Server
        try (Connection conn = getDbConnection()) {
            if (conn != null && !conn.isClosed()) {
                isSqlServerAvailable = true;
                System.out.println("[DB] Kết nối Microsoft SQL Server THÀNH CÔNG!");
            }
        } catch (Exception e) {
            isSqlServerAvailable = false;
            System.out.println("[DB] Chưa bật SQL Server -> Tự động chạy với In-Memory Dataset sẵn sàng.");
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // 1. MODULE IDENTITY (UC 1, 2, 12)
        server.createContext("/api/auth/register", new AuthRegisterHandler());
        server.createContext("/api/auth/login", new AuthLoginHandler());
        server.createContext("/api/register", new AuthRegisterHandler()); // Tương thích cũ
        server.createContext("/api/login", new AuthLoginHandler());       // Tương thích cũ
        server.createContext("/api/auth/profile", new AuthProfileHandler());
        server.createContext("/api/auth/change-password", new AuthChangePasswordHandler());

        // 2. MODULE CATALOG (UC 8)
        server.createContext("/api/courts", new CatalogCourtsHandler());

        // 3. MODULE BOOKING (UC 9, 10, 11)
        server.createContext("/api/bookings", new BookingHandler());

        // 4. MODULE OWNER (UC 3: Quản lý sân, UC 5: Lịch và đơn đặt)
        server.createContext("/api/owner/courts", new OwnerCourtsHandler());
        server.createContext("/api/owner/bookings", new OwnerBookingsHandler());

        // 5. MODULE PAYMENT (UC 6: Thanh toán và báo cáo)
        server.createContext("/api/owner/payments", new PaymentHandler());
        server.createContext("/api/owner/reports", new ReportHandler());

        // 6. MODULE ADMIN (UC 4: Quản lý tài khoản, UC 7: Giám sát toàn hệ thống)
        server.createContext("/api/admin/users", new AdminUsersHandler());
        server.createContext("/api/admin/courts", new AdminCourtsHandler());
        server.createContext("/api/admin/bookings", new AdminBookingsHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("-> CÁC API SẴN SÀNG:");
        System.out.println("   * [Identity] POST /api/auth/register, POST /api/auth/login");
        System.out.println("   * [Catalog]  GET  /api/courts");
        System.out.println("   * [Booking]  POST /api/bookings, GET /api/bookings");
        System.out.println("   * [Owner]    GET/POST/PUT/DELETE /api/owner/courts, /api/owner/bookings");
        System.out.println("   * [Payment]  GET/POST /api/owner/payments, GET /api/owner/reports");
        System.out.println("   * [Admin]    GET/POST/DELETE /api/admin/users, /api/admin/courts");
        System.out.println("=================================================================");
    }

    // =========================================================================
    // HANDLERS
    // =========================================================================

    // UC 1: ĐĂNG KÝ
    static class AuthRegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(405, -1); return; }

            String body = readBody(ex);
            String name = getField(body, "hoVaTen");
            String phone = getField(body, "soDienThoai");
            String email = getField(body, "email");
            String pass = getField(body, "matKhau");

            if (name.isEmpty()) { sendJson(ex, 400, "{\"success\":false,\"message\":\"Nhập họ và tên\"}"); return; }
            if (!Pattern.matches("^(0)(3|5|7|8|9)[0-9]{8}$", phone)) { sendJson(ex, 400, "{\"success\":false,\"message\":\"Số điện thoại di động không đúng\"}"); return; }
            if (!email.contains("@")) { sendJson(ex, 400, "{\"success\":false,\"message\":\"Email không hợp lệ, vui lòng nhập lại\"}"); return; }
            if (pass.length() < 8) { sendJson(ex, 400, "{\"success\":false,\"message\":\"Mật khẩu có độ dài tối thiểu 8 ký tự\"}"); return; }

            for (Account a : accounts) {
                if (a.email.equalsIgnoreCase(email) || a.phone.equals(phone)) {
                    sendJson(ex, 400, "{\"success\":false,\"message\":\"Số điện thoại hoặc Email đã tồn tại\"}"); return;
                }
            }

            String newId = "TK" + String.format("%03d", accounts.size() + 1);
            Account newAcc = new Account(newId, name, phone, email, pass, "Khách hàng", "Hoạt động");
            accounts.add(newAcc);

            if (isSqlServerAvailable) {
                try (Connection conn = getDbConnection();
                     PreparedStatement ps = conn.prepareStatement("INSERT INTO TAIKHOAN(HoVaTen, SoDienThoai, Email, MatKhau, VaiTro) VALUES(?,?,?,?,?)")) {
                    ps.setNString(1, name); ps.setString(2, phone); ps.setString(3, email); ps.setString(4, pass); ps.setNString(5, "Khách hàng");
                    ps.executeUpdate();
                } catch (Exception ignored) {}
            }

            sendJson(ex, 200, "{\"success\":true,\"message\":\"Đăng ký thành công\"}");
        }
    }

    // UC 2: ĐĂNG NHẬP
    static class AuthLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(405, -1); return; }

            String body = readBody(ex);
            String identifier = getField(body, "identifier");
            String password = getField(body, "password");

            Account matched = null;
            for (Account a : accounts) {
                if ((a.email.equalsIgnoreCase(identifier) || a.phone.equals(identifier)) && a.password.equals(password)) {
                    matched = a; break;
                }
            }

            if (matched != null) {
                String res = String.format("{\"success\":true,\"message\":\"Đăng nhập thành công\",\"user\":{\"id\":\"%s\",\"hoVaTen\":\"%s\",\"email\":\"%s\",\"vaiTro\":\"%s\"}}",
                    matched.id, esc(matched.name), esc(matched.email), esc(matched.role));
                sendJson(ex, 200, res);
            } else {
                sendJson(ex, 400, "{\"success\":false,\"message\":\"Đăng nhập thất bại, vui lòng thử lại\"}");
            }
        }
    }

    // UC 12: QUẢN LÝ TÀI KHOẢN CÁ NHÂN (Hồ sơ)
    static class AuthProfileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            if ("PUT".equalsIgnoreCase(ex.getRequestMethod())) {
                String body = readBody(ex);
                String id = getField(body, "id");
                String name = getField(body, "hoVaTen");
                for (Account a : accounts) {
                    if (a.id.equals(id) || a.email.equalsIgnoreCase(id)) {
                        a.name = name;
                        sendJson(ex, 200, "{\"success\":true,\"message\":\"Cập nhật thông tin thành công\"}");
                        return;
                    }
                }
                sendJson(ex, 404, "{\"success\":false,\"message\":\"Không tìm thấy tài khoản\"}");
            }
        }
    }

    // UC 12: ĐỔI MẬT KHẨU
    static class AuthChangePasswordHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
                String body = readBody(ex);
                String id = getField(body, "id");
                String oldPass = getField(body, "oldPassword");
                String newPass = getField(body, "newPassword");
                for (Account a : accounts) {
                    if ((a.id.equals(id) || a.email.equalsIgnoreCase(id)) && a.password.equals(oldPass)) {
                        a.password = newPass;
                        sendJson(ex, 200, "{\"success\":true,\"message\":\"Đổi mật khẩu thành công\"}");
                        return;
                    }
                }
                sendJson(ex, 400, "{\"success\":false,\"message\":\"Mật khẩu hiện tại không đúng\"}");
            }
        }
    }

    // UC 8: TÌM KIẾM VÀ XEM SÂN
    static class CatalogCourtsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < courts.size(); i++) {
                Court c = courts.get(i);
                if (!c.visible) continue;
                if (json.length() > 1) json.append(",");
                json.append(String.format("{\"id\":\"%s\",\"name\":\"%s\",\"facility\":\"%s\",\"address\":\"%s\",\"type\":\"%s\",\"price\":%f,\"open\":\"%s\",\"close\":\"%s\",\"status\":\"%s\"}",
                    c.id, esc(c.name), esc(c.facility), esc(c.address), esc(c.type), c.price, c.open, c.close, c.operational));
            }
            json.append("]");
            sendJson(ex, 200, json.toString());
        }
    }

    // UC 9, 10, 11: ĐẶT SÂN & ĐƠN ĐẶT CỦA KHÁCH HÀNG
    static class BookingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
                String body = readBody(ex);
                String accId = getField(body, "accountId");
                String courtId = getField(body, "courtId");
                String date = getField(body, "date");
                String start = getField(body, "start");
                String end = getField(body, "end");

                String newId = "D" + String.format("%03d", bookings.size() + 1);
                BookingOrder bo = new BookingOrder(newId, accId, "Khách hàng", courtId, date, start, end, 300000, 0, 0, "Chờ xác nhận", "");
                bookings.add(bo);
                sendJson(ex, 200, "{\"success\":true,\"message\":\"Đặt sân thành công\",\"orderId\":\"" + newId + "\"}");
            } else {
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < bookings.size(); i++) {
                    BookingOrder b = bookings.get(i);
                    if (i > 0) json.append(",");
                    json.append(String.format("{\"id\":\"%s\",\"courtId\":\"%s\",\"date\":\"%s\",\"start\":\"%s\",\"end\":\"%s\",\"rent\":%f,\"status\":\"%s\"}",
                        b.id, b.courtId, b.date, b.start, b.end, b.rent, b.status));
                }
                json.append("]");
                sendJson(ex, 200, json.toString());
            }
        }
    }

    // UC 3: QUẢN LÝ SÂN THỂ THAO (Chủ sân)
    static class OwnerCourtsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            String method = ex.getRequestMethod();
            if ("GET".equalsIgnoreCase(method)) {
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < courts.size(); i++) {
                    Court c = courts.get(i);
                    if (i > 0) json.append(",");
                    json.append(String.format("{\"id\":\"%s\",\"name\":\"%s\",\"facility\":\"%s\",\"address\":\"%s\",\"type\":\"%s\",\"price\":%f,\"open\":\"%s\",\"close\":\"%s\",\"operational\":\"%s\",\"visible\":%b}",
                        c.id, esc(c.name), esc(c.facility), esc(c.address), esc(c.type), c.price, c.open, c.close, c.operational, c.visible));
                }
                json.append("]");
                sendJson(ex, 200, json.toString());
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = readBody(ex);
                String id = getField(body, "id");
                String name = getField(body, "name");
                String facility = getField(body, "facility");
                String address = getField(body, "address");
                String type = getField(body, "type");
                double price = Double.parseDouble(getField(body, "price", "300000"));
                String open = getField(body, "open", "06:00");
                String close = getField(body, "close", "22:00");
                courts.add(new Court(id, name, facility, address, type, price, open, close, "Đang hoạt động", true, "TK002", "Chủ sân", ""));
                sendJson(ex, 200, "{\"success\":true,\"message\":\"Thêm sân thành công!\"}");
            }
        }
    }

    // UC 5: LỊCH VÀ ĐƠN ĐẶT (Chủ sân)
    static class OwnerBookingsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            sendJson(ex, 200, "{\"success\":true,\"bookingsCount\":" + bookings.size() + "}");
        }
    }

    // UC 6: THANH TOÁN (Chủ sân)
    static class PaymentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            sendJson(ex, 200, "{\"success\":true,\"transactionsCount\":" + transactions.size() + "}");
        }
    }

    // UC 6: BÁO CÁO (Chủ sân)
    static class ReportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            sendJson(ex, 200, "{\"success\":true,\"revenue\":12800000,\"refund\":850000,\"orders\":64,\"cancelled\":5,\"utilization\":62}");
        }
    }

    // UC 4: QUẢN LÝ TÀI KHOẢN (Quản trị viên)
    static class AdminUsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < accounts.size(); i++) {
                    Account a = accounts.get(i);
                    if (i > 0) json.append(",");
                    json.append(String.format("{\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"role\":\"%s\",\"status\":\"%s\"}",
                        a.id, esc(a.name), esc(a.email), esc(a.phone), a.role, a.status));
                }
                json.append("]");
                sendJson(ex, 200, json.toString());
            } else if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
                String body = readBody(ex);
                String id = "TK" + String.format("%03d", accounts.size() + 1);
                String name = getField(body, "name");
                String email = getField(body, "email");
                String phone = getField(body, "phone");
                String pass = getField(body, "password", "12345678");
                String role = getField(body, "role", "Chủ sân");
                accounts.add(new Account(id, name, phone, email, pass, role, "Hoạt động"));
                sendJson(ex, 200, "{\"success\":true,\"message\":\"Thêm tài khoản thành công!\"}");
            }
        }
    }

    // UC 7: GIÁM SÁT SÂN TOÀN HỆ THỐNG (Admin)
    static class AdminCourtsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            sendJson(ex, 200, "{\"success\":true,\"courtsCount\":" + courts.size() + "}");
        }
    }

    // UC 7: GIÁM SÁT ĐƠN ĐẶT TOÀN HỆ THỐNG (Admin)
    static class AdminBookingsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            enableCORS(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); return; }
            sendJson(ex, 200, "{\"success\":true,\"ordersCount\":" + bookings.size() + "}");
        }
    }

    // =========================================================================
    // TIỆN ÍCH
    // =========================================================================
    private static void enableCORS(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static String readBody(HttpExchange ex) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private static void sendJson(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    private static String getField(String json, String field) { return getField(json, field, ""); }
    private static String getField(String json, String field, String def) {
        String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Matcher m = Pattern.compile(pattern).matcher(json);
        if (m.find()) return m.group(1);
        String numPattern = "\"" + field + "\"\\s*:\\s*([0-9.]+)";
        java.util.regex.Matcher m2 = Pattern.compile(numPattern).matcher(json);
        if (m2.find()) return m2.group(1);
        return def;
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
