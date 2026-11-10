package com.sportbooking.modules.owner.controller;

import com.sportbooking.modules.booking.model.BookingOrder;
import com.sportbooking.modules.owner.service.OwnerService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * Endpoint quản lý lịch và đơn đặt cho Chủ sân SportBooking
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

        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            String body = HttpUtils.readBody(ex);
            String orderId = HttpUtils.getField(body, "orderId");
            String status = HttpUtils.getField(body, "status", "Đã xác nhận");
            boolean ok = ownerService.updateBookingStatus(orderId, status);
            HttpUtils.sendJson(ex, 200, "{\"success\":" + ok + ",\"message\":\"Cập nhật trạng thái thành công\"}");
            return;
        }

        List<BookingOrder> bookings = ownerService.getAllBookings();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < bookings.size(); i++) {
            BookingOrder b = bookings.get(i);
            if (i > 0) json.append(",");
            json.append(String.format(
                    "{\"id\":\"%s\",\"customer\":\"%s\",\"customerName\":\"%s\",\"court\":\"%s\",\"courtId\":\"%s\",\"date\":\"%s\",\"time\":\"%s - %s\",\"start\":\"%s\",\"end\":\"%s\",\"total\":%.0f,\"status\":\"%s\",\"note\":\"%s\"}",
                    b.id, HttpUtils.escape(b.customerName), HttpUtils.escape(b.customerName),
                    b.courtId, b.courtId, b.date, b.start, b.end, b.start, b.end, b.rent, b.status, HttpUtils.escape(b.reason)
            ));
        }
        json.append("]");
        HttpUtils.sendJson(ex, 200, json.toString());
    }
}
