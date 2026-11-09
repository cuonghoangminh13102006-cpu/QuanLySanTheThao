package com.sportbooking.modules.booking.model;

/**
 * Model Đơn đặt sân
 * Tương ứng bảng DONDATSAN và booking.bookings trong anta-backend
 */
public class BookingOrder {
    public String id;
    public String accountId;
    public String customerName;
    public String courtId;
    public String date;
    public String start;
    public String end;
    public double rent;
    public double service;
    public double deposit;
    public String status;
    public String reason;

    public BookingOrder(String id, String accountId, String customerName, String courtId,
                        String date, String start, String end,
                        double rent, double service, double deposit,
                        String status, String reason) {
        this.id = id;
        this.accountId = accountId;
        this.customerName = customerName;
        this.courtId = courtId;
        this.date = date;
        this.start = start;
        this.end = end;
        this.rent = rent;
        this.service = service;
        this.deposit = deposit;
        this.status = status;
        this.reason = reason;
    }
}
