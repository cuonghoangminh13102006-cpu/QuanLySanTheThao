package com.sportbooking.modules.admin.controller;

import com.sportbooking.modules.admin.service.AdminService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Endpoint giám sát sân thể thao toàn hệ thống (UC 7)
 */
public class AdminCourtsHandler implements HttpHandler {
    private final AdminService adminService;

    public AdminCourtsHandler(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        HttpUtils.enableCORS(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        int count = adminService.getCourtsCount();
        HttpUtils.sendJson(ex, 200, "{\"success\":true,\"courtsCount\":" + count + "}");
    }
}
