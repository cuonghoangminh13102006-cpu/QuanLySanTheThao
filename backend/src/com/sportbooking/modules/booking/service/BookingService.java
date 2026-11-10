package com.sportbooking.modules.booking.service;

import com.sportbooking.modules.booking.model.BookingOrder;
import com.sportbooking.shared.database.InMemoryStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Nghiệp vụ đặt sân SportBooking
 */
public class BookingService {

    public BookingOrder createBooking(String id, String accId, String customerName, String courtId,
                                      String date, String start, String end, double rent, String status, String note) {
        synchronized (InMemoryStore.bookings) {
            String newId = (id != null && !id.trim().isEmpty()) ? id.trim() : "SB-" + System.currentTimeMillis() % 1000000;
            String st = (status != null && !status.trim().isEmpty()) ? status.trim() : "Đã xác nhận";
            BookingOrder bo = new BookingOrder(newId, accId, customerName, courtId, date, start, end, rent, 0, rent, st, note);
            InMemoryStore.bookings.add(0, bo);
            return bo;
        }
    }

    public BookingOrder createBooking(String accId, String courtId, String date, String start, String end) {
        return createBooking(null, accId, "Khách hàng", courtId, date, start, end, 300000, "Đã xác nhận", "");
    }

    public boolean cancelBooking(String orderId) {
        synchronized (InMemoryStore.bookings) {
            for (BookingOrder b : InMemoryStore.bookings) {
                if (b.id.equalsIgnoreCase(orderId)) {
                    b.status = "Đã hủy";
                    return true;
                }
            }
        }
        return false;
    }

    public boolean updateStatus(String orderId, String status) {
        synchronized (InMemoryStore.bookings) {
            for (BookingOrder b : InMemoryStore.bookings) {
                if (b.id.equalsIgnoreCase(orderId)) {
                    b.status = status;
                    return true;
                }
            }
        }
        return false;
    }

    public List<BookingOrder> getAllBookings() {
        synchronized (InMemoryStore.bookings) {
            return new ArrayList<>(InMemoryStore.bookings);
        }
    }

    public List<BookingOrder> getBookingsByAccount(String accId) {
        List<BookingOrder> list = new ArrayList<>();
        synchronized (InMemoryStore.bookings) {
            for (BookingOrder b : InMemoryStore.bookings) {
                if (b.accountId.equalsIgnoreCase(accId)) {
                    list.add(b);
                }
            }
        }
        return list;
    }
}
