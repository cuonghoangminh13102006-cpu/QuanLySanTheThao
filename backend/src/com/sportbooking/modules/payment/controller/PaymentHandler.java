package com.sportbooking.modules.payment.controller;

import com.sportbooking.modules.payment.service.PaymentService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Endpoint quản lý thanh toán cho Chủ sân (UC 6)
 */
public class PaymentHandler implements HttpHandler {
    private final PaymentService paymentService;

    public PaymentHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        HttpUtils.enableCORS(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        int count = paymentService.getTransactionsCount();
        HttpUtils.sendJson(ex, 200, "{\"success\":true,\"transactionsCount\":" + count + "}");
    }
}
