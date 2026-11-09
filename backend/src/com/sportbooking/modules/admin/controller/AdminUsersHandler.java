package com.sportbooking.modules.admin.controller;

import com.sportbooking.modules.admin.service.AdminService;
import com.sportbooking.modules.identity.model.Account;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * Endpoint quản lý người dùng cho Quản trị viên (UC 4)
 */
public class AdminUsersHandler implements HttpHandler {
    private final AdminService adminService;

    public AdminUsersHandler(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        HttpUtils.enableCORS(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        String method = ex.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            List<Account> accounts = adminService.getAllUsers();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < accounts.size(); i++) {
                Account a = accounts.get(i);
                if (i > 0) json.append(",");
                json.append(String.format(
                        "{\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"role\":\"%s\",\"status\":\"%s\"}",
                        a.id, HttpUtils.escape(a.name), HttpUtils.escape(a.email), HttpUtils.escape(a.phone),
                        HttpUtils.escape(a.role), HttpUtils.escape(a.status)
                ));
            }
            json.append("]");
            HttpUtils.sendJson(ex, 200, json.toString());
        } else if ("POST".equalsIgnoreCase(method)) {
            String body = HttpUtils.readBody(ex);
            String name = HttpUtils.getField(body, "name");
            String email = HttpUtils.getField(body, "email");
            String phone = HttpUtils.getField(body, "phone");
            String pass = HttpUtils.getField(body, "password", "12345678");
            String role = HttpUtils.getField(body, "role", "Chủ sân");

            adminService.createUser(name, email, phone, pass, role);
            HttpUtils.sendJson(ex, 200, "{\"success\":true,\"message\":\"Thêm tài khoản thành công!\"}");
        } else {
            ex.sendResponseHeaders(405, -1);
        }
    }
}
