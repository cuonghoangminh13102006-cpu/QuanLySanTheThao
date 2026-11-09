package com.sportbooking.modules.booking.controller;

import com.sportbooking.modules.booking.model.BookingOrder;
import com.sportbooking.modules.booking.service.BookingService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * Endpoint đặt sân & theo dõi đơn của khách (UC 9, UC 10, UC 11)
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
            String accId = HttpUtils.getField(body, "accountId");
            String courtId = HttpUtils.getField(body, "courtId");
            String date = HttpUtils.getField(body, "date");
            String start = HttpUtils.getField(body, "start");
            String end = HttpUtils.getField(body, "end");

            BookingOrder bo = bookingService.createBooking(accId, courtId, date, start, end);
            HttpUtils.sendJson(ex, 200, "{\"success\":true,\"message\":\"Đặt sân thành công\",\"orderId\":\"" + bo.id + "\"}");
        } else {
            List<BookingOrder> bookings = bookingService.getAllBookings();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < bookings.size(); i++) {
                BookingOrder b = bookings.get(i);
                if (i > 0) json.append(",");
                json.append(String.format(
                        "{\"id\":\"%s\",\"courtId\":\"%s\",\"date\":\"%s\",\"start\":\"%s\",\"end\":\"%s\",\"rent\":%.0f,\"status\":\"%s\"}",
                        b.id, b.courtId, b.date, b.start, b.end, b.rent, b.status
                ));
            }
            json.append("]");
            HttpUtils.sendJson(ex, 200, json.toString());
        }
    }
}
