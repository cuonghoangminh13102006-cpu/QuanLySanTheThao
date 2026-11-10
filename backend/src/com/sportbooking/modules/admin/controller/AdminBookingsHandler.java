package com.sportbooking.modules.admin.controller;

import com.sportbooking.modules.admin.service.AdminService;
import com.sportbooking.modules.booking.model.BookingOrder;
import com.sportbooking.shared.database.InMemoryStore;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * Endpoint giám sát đơn đặt sân toàn hệ thống SportBooking
 */
public class AdminBookingsHandler implements HttpHandler {
    private final AdminService adminService;

    public AdminBookingsHandler(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        HttpUtils.enableCORS(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        List<BookingOrder> bookings;
        synchronized (InMemoryStore.bookings) {
            bookings = new java.util.ArrayList<>(InMemoryStore.bookings);
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < bookings.size(); i++) {
            BookingOrder b = bookings.get(i);
            if (i > 0) json.append(",");
            json.append(String.format(
                    "{\"id\":\"%s\",\"customer\":\"%s\",\"customerName\":\"%s\",\"court\":\"%s\",\"courtId\":\"%s\",\"date\":\"%s\",\"time\":\"%s - %s\",\"total\":%.0f,\"status\":\"%s\",\"note\":\"%s\"}",
                    b.id, HttpUtils.escape(b.customerName), HttpUtils.escape(b.customerName),
                    b.courtId, b.courtId, b.date, b.start, b.end, b.rent, b.status, HttpUtils.escape(b.reason)
            ));
        }
        json.append("]");
        HttpUtils.sendJson(ex, 200, json.toString());
    }
}
