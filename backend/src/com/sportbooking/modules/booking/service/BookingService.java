package com.sportbooking.modules.booking.service;

import com.sportbooking.modules.booking.model.BookingOrder;
import com.sportbooking.shared.database.InMemoryStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Nghiệp vụ đặt sân (UC 9, UC 10, UC 11)
 * Tương ứng modules/booking/service.ts trong anta-backend
 */
public class BookingService {

    public BookingOrder createBooking(String accId, String courtId, String date, String start, String end) {
        synchronized (InMemoryStore.bookings) {
            String newId = "D" + String.format("%03d", InMemoryStore.bookings.size() + 1);
            BookingOrder bo = new BookingOrder(newId, accId, "Khách hàng", courtId, date, start, end, 300000, 0, 0, "Chờ xác nhận", "");
            InMemoryStore.bookings.add(bo);
            return bo;
        }
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
