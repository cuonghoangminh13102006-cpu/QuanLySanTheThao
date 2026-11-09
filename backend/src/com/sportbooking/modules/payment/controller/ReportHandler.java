package com.sportbooking.modules.payment.controller;

import com.sportbooking.modules.payment.service.PaymentService;
import com.sportbooking.shared.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Endpoint báo cáo tài chính cho Chủ sân (UC 6)
 */
public class ReportHandler implements HttpHandler {
    private final PaymentService paymentService;

    public ReportHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        HttpUtils.enableCORS(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        String json = paymentService.getRevenueReportJson();
        HttpUtils.sendJson(ex, 200, json);
    }
}
