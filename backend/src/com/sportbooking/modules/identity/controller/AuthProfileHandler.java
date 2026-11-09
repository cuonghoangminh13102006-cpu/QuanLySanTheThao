package com.sportbooking.modules.identity.controller;

import com.sportbooking.modules.identity.service.IdentityService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Endpoint cập nhật thông tin cá nhân (UC 12)
 */
public class AuthProfileHandler implements HttpHandler {
    private final IdentityService identityService;

    public AuthProfileHandler(IdentityService identityService) {
        this.identityService = identityService;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        HttpUtils.enableCORS(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }
        if (!"PUT".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(405, -1);
            return;
        }

        String body = HttpUtils.readBody(ex);
        String id = HttpUtils.getField(body, "id");
        String name = HttpUtils.getField(body, "hoVaTen");

        if (identityService.updateProfile(id, name)) {
            HttpUtils.sendJson(ex, 200, "{\"success\":true,\"message\":\"Cập nhật thông tin thành công\"}");
        } else {
            HttpUtils.sendJson(ex, 404, "{\"success\":false,\"message\":\"Không tìm thấy tài khoản\"}");
        }
    }
}
