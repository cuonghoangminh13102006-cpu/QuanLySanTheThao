package com.sportbooking.modules.owner.service;

import com.sportbooking.modules.catalog.model.Court;
import com.sportbooking.modules.booking.model.BookingOrder;
import com.sportbooking.shared.database.InMemoryStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Nghiệp vụ quản lý của Chủ sân SportBooking
 */
public class OwnerService {

    public List<Court> getOwnerCourts() {
        synchronized (InMemoryStore.courts) {
            return new ArrayList<>(InMemoryStore.courts);
        }
    }

    public Court addCourt(String id, String name, String facility, String address, String type,
                          double price, String open, String close) {
        synchronized (InMemoryStore.courts) {
            String newId = (id != null && !id.isEmpty()) ? id : "S" + String.format("%03d", InMemoryStore.courts.size() + 1);
            Court c = new Court(newId, name, facility, address, type, price, open, close,
                    "Đang hoạt động", true, "TK002", "Chủ sân", "");
            InMemoryStore.courts.add(c);
            return c;
        }
    }

    public List<BookingOrder> getAllBookings() {
        synchronized (InMemoryStore.bookings) {
            return new ArrayList<>(InMemoryStore.bookings);
        }
    }

    public boolean updateBookingStatus(String orderId, String status) {
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

    public int getBookingsCount() {
        synchronized (InMemoryStore.bookings) {
            return InMemoryStore.bookings.size();
        }
    }
}
