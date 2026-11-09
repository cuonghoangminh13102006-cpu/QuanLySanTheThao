package com.sportbooking.modules.identity.controller;

import com.sportbooking.modules.identity.service.IdentityService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Endpoint đăng ký tài khoản (UC 1)
 */
public class AuthRegisterHandler implements HttpHandler {
    private final IdentityService identityService;

    public AuthRegisterHandler(IdentityService identityService) {
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
        String name = HttpUtils.getField(body, "hoVaTen");
        String phone = HttpUtils.getField(body, "soDienThoai");
        String email = HttpUtils.getField(body, "email");
        String pass = HttpUtils.getField(body, "matKhau");

        IdentityService.AuthResult res = identityService.register(name, phone, email, pass);
        if (res.success) {
            HttpUtils.sendJson(ex, 200, "{\"success\":true,\"message\":\"" + HttpUtils.escape(res.message) + "\"}");
        } else {
            HttpUtils.sendJson(ex, 400, "{\"success\":false,\"message\":\"" + HttpUtils.escape(res.message) + "\"}");
        }
    }
}
