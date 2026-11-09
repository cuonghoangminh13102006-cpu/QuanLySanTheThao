package com.sportbooking.modules.payment.model;

/**
 * Model Giao dịch thanh toán
 * Tương ứng bảng GIAODICH và payment.transactions trong anta-backend
 */
public class TransactionLog {
    public String id;
    public String bookingId;
    public String type;
    public double amount;
    public String method;
    public String date;
    public String by;

    public TransactionLog(String id, String bookingId, String type, double amount, String method, String date, String by) {
        this.id = id;
        this.bookingId = bookingId;
        this.type = type;
        this.amount = amount;
        this.method = method;
        this.date = date;
        this.by = by;
    }
}
