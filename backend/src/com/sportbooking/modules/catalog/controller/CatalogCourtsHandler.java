package com.sportbooking.modules.catalog.controller;

import com.sportbooking.modules.catalog.model.Court;
import com.sportbooking.modules.catalog.service.CatalogService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * Endpoint danh sách sân công khai cho khách hàng (UC 8)
 */
public class CatalogCourtsHandler implements HttpHandler {
    private final CatalogService catalogService;

    public CatalogCourtsHandler(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        HttpUtils.enableCORS(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        List<Court> courts = catalogService.getPublicCourts();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < courts.size(); i++) {
            Court c = courts.get(i);
            if (i > 0) json.append(",");
            json.append(String.format(
                    "{\"id\":\"%s\",\"name\":\"%s\",\"facility\":\"%s\",\"address\":\"%s\",\"type\":\"%s\",\"price\":%.0f,\"open\":\"%s\",\"close\":\"%s\",\"status\":\"%s\"}",
                    c.id, HttpUtils.escape(c.name), HttpUtils.escape(c.facility), HttpUtils.escape(c.address),
                    HttpUtils.escape(c.type), c.price, c.open, c.close, c.operational
            ));
        }
        json.append("]");

        HttpUtils.sendJson(ex, 200, json.toString());
    }
}
