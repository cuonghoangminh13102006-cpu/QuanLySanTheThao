package com.sportbooking.modules.owner.controller;

import com.sportbooking.modules.catalog.model.Court;
import com.sportbooking.modules.owner.service.OwnerService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * Endpoint quản lý sân cho Chủ sân (UC 3)
 */
public class OwnerCourtsHandler implements HttpHandler {
    private final OwnerService ownerService;

    public OwnerCourtsHandler(OwnerService ownerService) {
        this.ownerService = ownerService;
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
            List<Court> courts = ownerService.getOwnerCourts();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < courts.size(); i++) {
                Court c = courts.get(i);
                if (i > 0) json.append(",");
                json.append(String.format(
                        "{\"id\":\"%s\",\"name\":\"%s\",\"facility\":\"%s\",\"address\":\"%s\",\"type\":\"%s\",\"price\":%.0f,\"open\":\"%s\",\"close\":\"%s\",\"operational\":\"%s\",\"visible\":%b}",
                        c.id, HttpUtils.escape(c.name), HttpUtils.escape(c.facility), HttpUtils.escape(c.address),
                        HttpUtils.escape(c.type), c.price, c.open, c.close, c.operational, c.visible
                ));
            }
            json.append("]");
            HttpUtils.sendJson(ex, 200, json.toString());
        } else if ("POST".equalsIgnoreCase(method)) {
            String body = HttpUtils.readBody(ex);
            String id = HttpUtils.getField(body, "id");
            String name = HttpUtils.getField(body, "name");
            String facility = HttpUtils.getField(body, "facility");
            String address = HttpUtils.getField(body, "address");
            String type = HttpUtils.getField(body, "type");
            double price = Double.parseDouble(HttpUtils.getField(body, "price", "300000"));
            String open = HttpUtils.getField(body, "open", "06:00");
            String close = HttpUtils.getField(body, "close", "22:00");

            ownerService.addCourt(id, name, facility, address, type, price, open, close);
            HttpUtils.sendJson(ex, 200, "{\"success\":true,\"message\":\"Thêm sân thành công!\"}");
        } else {
            ex.sendResponseHeaders(405, -1);
        }
    }
}
