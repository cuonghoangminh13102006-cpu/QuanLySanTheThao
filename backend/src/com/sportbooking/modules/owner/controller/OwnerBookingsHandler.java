package com.sportbooking.modules.owner.controller;

import com.sportbooking.modules.owner.service.OwnerService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Endpoint quản lý lịch và đơn đặt cho Chủ sân (UC 5)
 */
public class OwnerBookingsHandler implements HttpHandler {
    private final OwnerService ownerService;

    public OwnerBookingsHandler(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        HttpUtils.enableCORS(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        int count = ownerService.getBookingsCount();
        HttpUtils.sendJson(ex, 200, "{\"success\":true,\"bookingsCount\":" + count + "}");
    }
}
