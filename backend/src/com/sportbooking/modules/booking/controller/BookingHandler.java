package com.sportbooking.modules.booking.controller;

import com.sportbooking.modules.booking.model.BookingOrder;
import com.sportbooking.modules.booking.service.BookingService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * Endpoint đặt sân & theo dõi đơn của khách (SportBooking)
 */
public class BookingHandler implements HttpHandler {
    private final BookingService bookingService;

    public BookingHandler(BookingService bookingService) {
        this.bookingService = bookingService;
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
            String action = HttpUtils.getField(body, "action");
            String orderId = HttpUtils.getField(body, "orderId");

            if ("cancel".equalsIgnoreCase(action) || (orderId != null && !orderId.isEmpty() && body.contains("\"cancel\""))) {
                boolean ok = bookingService.cancelBooking(orderId);
                HttpUtils.sendJson(ex, 200, "{\"success\":" + ok + ",\"message\":\"Đã hủy đơn đặt thành công\"}");
                return;
            }

            String id = HttpUtils.getField(body, "id");
            String accId = HttpUtils.getField(body, "accountId", "TK003");
            String customerName = HttpUtils.getField(body, "customer", "Khách hàng");
            if (customerName.equals("Khách hàng")) customerName = HttpUtils.getField(body, "customerName", "Khách hàng");
            String courtId = HttpUtils.getField(body, "courtId", "S001");
            if (courtId.equals("S001")) courtId = HttpUtils.getField(body, "court", "S001");
            String date = HttpUtils.getField(body, "date");
            if (date == null || date.isEmpty()) date = HttpUtils.getField(body, "dateString");
            String start = HttpUtils.getField(body, "start", "18:00");
            if (start.equals("18:00")) start = HttpUtils.getField(body, "startTime", "18:00");
            String end = HttpUtils.getField(body, "end", "19:00");
            if (end.equals("19:00")) end = HttpUtils.getField(body, "endTime", "19:00");
            String rentStr = HttpUtils.getField(body, "rent", "300000");
            if (rentStr.equals("300000")) rentStr = HttpUtils.getField(body, "total", "300000");
            double rent = 300000;
            try { rent = Double.parseDouble(rentStr); } catch (Exception ignored) {}
            String status = HttpUtils.getField(body, "status", "Đã xác nhận");
            String note = HttpUtils.getField(body, "note", "");

            BookingOrder bo = bookingService.createBooking(id, accId, customerName, courtId, date, start, end, rent, status, note);
            HttpUtils.sendJson(ex, 200, "{\"success\":true,\"message\":\"Đặt sân thành công\",\"orderId\":\"" + bo.id + "\"}");
        } else {
            List<BookingOrder> bookings = bookingService.getAllBookings();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < bookings.size(); i++) {
                BookingOrder b = bookings.get(i);
                if (i > 0) json.append(",");
                json.append(String.format(
                        "{\"id\":\"%s\",\"accountId\":\"%s\",\"customerName\":\"%s\",\"customer\":\"%s\",\"courtId\":\"%s\",\"court\":\"%s\",\"date\":\"%s\",\"dateString\":\"%s\",\"start\":\"%s\",\"startTime\":\"%s\",\"end\":\"%s\",\"endTime\":\"%s\",\"time\":\"%s - %s\",\"rent\":%.0f,\"total\":%.0f,\"status\":\"%s\",\"reason\":\"%s\"}",
                        b.id, b.accountId, HttpUtils.escape(b.customerName), HttpUtils.escape(b.customerName),
                        b.courtId, b.courtId, b.date, b.date, b.start, b.start, b.end, b.end,
                        b.start, b.end, b.rent, b.rent, b.status, HttpUtils.escape(b.reason)
                ));
            }
            json.append("]");
            HttpUtils.sendJson(ex, 200, json.toString());
        }
    }
}
