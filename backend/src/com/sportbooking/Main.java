package com.sportbooking;

import com.sportbooking.modules.admin.controller.AdminBookingsHandler;
import com.sportbooking.modules.admin.controller.AdminCourtsHandler;
import com.sportbooking.modules.admin.controller.AdminUsersHandler;
import com.sportbooking.modules.admin.service.AdminService;
import com.sportbooking.modules.booking.controller.BookingHandler;
import com.sportbooking.modules.booking.service.BookingService;
import com.sportbooking.modules.catalog.controller.CatalogCourtsHandler;
import com.sportbooking.modules.catalog.service.CatalogService;
import com.sportbooking.modules.identity.controller.AuthChangePasswordHandler;
import com.sportbooking.modules.identity.controller.AuthLoginHandler;
import com.sportbooking.modules.identity.controller.AuthProfileHandler;
import com.sportbooking.modules.identity.controller.AuthRegisterHandler;
import com.sportbooking.modules.identity.service.IdentityService;
import com.sportbooking.modules.owner.controller.OwnerBookingsHandler;
import com.sportbooking.modules.owner.controller.OwnerCourtsHandler;
import com.sportbooking.modules.owner.service.OwnerService;
import com.sportbooking.modules.payment.controller.PaymentHandler;
import com.sportbooking.modules.payment.controller.ReportHandler;
import com.sportbooking.modules.payment.service.PaymentService;
import com.sportbooking.shared.config.AppConfig;
import com.sportbooking.shared.database.DatabaseManager;
import com.sportbooking.shared.http.StaticFileHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * HỆ THỐNG QUẢN LÝ SÂN THỂ THAO - BOOTSTRAP API & FRONTEND SERVER
 * Kiến trúc Modular Monolith dựa theo Anta Backend:
 * Phục vụ cả API backend lẫn toàn bộ giao diện Frontend tĩnh.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("=================================================================");
        System.out.println("   SPORTBOOKING FULLSTACK SERVER (PORT " + AppConfig.PORT + ")");
        System.out.println("   Hợp nhất toàn bộ Frontend & Backend theo Anta Architecture");
        System.out.println("=================================================================");

        // 1. Khởi tạo Database Manager
        DatabaseManager.initDatabase();

        // 2. Khởi tạo Services theo từng Domain Module
        IdentityService identityService = new IdentityService();
        CatalogService catalogService = new CatalogService();
        BookingService bookingService = new BookingService();
        OwnerService ownerService = new OwnerService();
        PaymentService paymentService = new PaymentService();
        AdminService adminService = new AdminService();

        // 3. Khởi tạo HTTP Server
        HttpServer server = HttpServer.create(new InetSocketAddress(AppConfig.PORT), 0);

        // --- Đăng ký Routes API theo Module ---

        // 1. MODULE IDENTITY (UC 1, 2, 12)
        server.createContext("/api/auth/register", new AuthRegisterHandler(identityService));
        server.createContext("/api/auth/login", new AuthLoginHandler(identityService));
        server.createContext("/api/register", new AuthRegisterHandler(identityService)); // Tương thích cũ
        server.createContext("/api/login", new AuthLoginHandler(identityService));       // Tương thích cũ
        server.createContext("/api/auth/profile", new AuthProfileHandler(identityService));
        server.createContext("/api/auth/change-password", new AuthChangePasswordHandler(identityService));

        // 2. MODULE CATALOG (UC 8)
        server.createContext("/api/courts", new CatalogCourtsHandler(catalogService));

        // 3. MODULE BOOKING (UC 9, 10, 11)
        server.createContext("/api/bookings", new BookingHandler(bookingService));

        // 4. MODULE OWNER (UC 3, 5)
        server.createContext("/api/owner/courts", new OwnerCourtsHandler(ownerService));
        server.createContext("/api/owner/bookings", new OwnerBookingsHandler(ownerService));

        // 5. MODULE PAYMENT (UC 6)
        server.createContext("/api/owner/payments", new PaymentHandler(paymentService));
        server.createContext("/api/owner/reports", new ReportHandler(paymentService));

        // 6. MODULE ADMIN (UC 4, 7)
        server.createContext("/api/admin/users", new AdminUsersHandler(adminService));
        server.createContext("/api/admin/courts", new AdminCourtsHandler(adminService));
        server.createContext("/api/admin/bookings", new AdminBookingsHandler(adminService));

        // --- 7. STATIC FILE HANDLER (Phục vụ toàn bộ giao diện Frontend) ---
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // default executor
        server.start();

        System.out.println("-> CÁC MODULE API SẴN SÀNG:");
        System.out.println("   [+] Identity Module : /api/auth/register, /api/auth/login, /api/auth/profile");
        System.out.println("   [+] Catalog Module  : /api/courts");
        System.out.println("   [+] Booking Module  : /api/bookings");
        System.out.println("   [+] Owner Module    : /api/owner/courts, /api/owner/bookings");
        System.out.println("   [+] Payment Module  : /api/owner/payments, /api/owner/reports");
        System.out.println("   [+] Admin Module    : /api/admin/users, /api/admin/courts, /api/admin/bookings");
        System.out.println("-> GIAO DIỆN FRONTEND ĐÃ ĐƯỢC GẮN THÀNH CÔNG:");
        System.out.println("   [+] Trang chủ Khách hàng   : http://localhost:" + AppConfig.PORT + "/");
        System.out.println("   [+] Cổng Chủ Sân (Anta)    : http://localhost:" + AppConfig.PORT + "/owner (hoặc /owner.html)");
        System.out.println("   [+] Cổng Quản Trị Sàn (Ad) : http://localhost:" + AppConfig.PORT + "/admin (hoặc /admin.html)");
        System.out.println("   [+] Đăng nhập / Đăng ký    : http://localhost:" + AppConfig.PORT + "/login.html");
        System.out.println("   [+] Cổng Quản lý cũ (HTML) : http://localhost:" + AppConfig.PORT + "/sportbooking-html/quan-ly-san.html");
        System.out.println("=================================================================");
    }
}
