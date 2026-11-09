package com.sportbooking.modules.payment.service;

import com.sportbooking.shared.database.InMemoryStore;

/**
 * Nghiệp vụ thanh toán & báo cáo doanh thu (UC 6)
 * Tương ứng modules/payment/service.ts trong anta-backend
 */
public class PaymentService {

    public int getTransactionsCount() {
        synchronized (InMemoryStore.transactions) {
            return InMemoryStore.transactions.size();
        }
    }

    public String getRevenueReportJson() {
        // Báo cáo doanh thu & tỷ lệ khai thác (tương thích format frontend)
        return "{\"success\":true,\"revenue\":12800000,\"refund\":850000,\"orders\":64,\"cancelled\":5,\"utilization\":62}";
    }
}
