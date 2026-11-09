package com.sportbooking.modules.identity.controller;

import com.sportbooking.modules.identity.model.Account;
import com.sportbooking.modules.identity.service.IdentityService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Endpoint đăng nhập tài khoản (UC 2)
 */
public class AuthLoginHandler implements HttpHandler {
    private final IdentityService identityService;

    public AuthLoginHandler(IdentityService identityService) {
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
        String identifier = HttpUtils.getField(body, "identifier");
        String password = HttpUtils.getField(body, "password");

        IdentityService.AuthResult res = identityService.login(identifier, password);
        if (res.success && res.account != null) {
            Account acc = res.account;
            String json = String.format(
                    "{\"success\":true,\"message\":\"Đăng nhập thành công\",\"user\":{\"id\":\"%s\",\"hoVaTen\":\"%s\",\"email\":\"%s\",\"vaiTro\":\"%s\"}}",
                    acc.id, HttpUtils.escape(acc.name), HttpUtils.escape(acc.email), HttpUtils.escape(acc.role)
            );
            HttpUtils.sendJson(ex, 200, json);
        } else {
            HttpUtils.sendJson(ex, 400, "{\"success\":false,\"message\":\"" + HttpUtils.escape(res.message) + "\"}");
        }
    }
}
