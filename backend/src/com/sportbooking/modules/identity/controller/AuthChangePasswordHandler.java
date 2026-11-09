package com.sportbooking.modules.identity.controller;

import com.sportbooking.modules.identity.service.IdentityService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Endpoint đổi mật khẩu (UC 12)
 */
public class AuthChangePasswordHandler implements HttpHandler {
    private final IdentityService identityService;

    public AuthChangePasswordHandler(IdentityService identityService) {
        this.identityService = identityService;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        HttpUtils.enableCORS(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(405, -1);
            return;
        }

        String body = HttpUtils.readBody(ex);
        String id = HttpUtils.getField(body, "id");
        String oldPass = HttpUtils.getField(body, "oldPassword");
        String newPass = HttpUtils.getField(body, "newPassword");

        if (identityService.changePassword(id, oldPass, newPass)) {
            HttpUtils.sendJson(ex, 200, "{\"success\":true,\"message\":\"Đổi mật khẩu thành công\"}");
        } else {
            HttpUtils.sendJson(ex, 400, "{\"success\":false,\"message\":\"Mật khẩu hiện tại không đúng hoặc mật khẩu mới quá ngắn\"}");
        }
    }
}
