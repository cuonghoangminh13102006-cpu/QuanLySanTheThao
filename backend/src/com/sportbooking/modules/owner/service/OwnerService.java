package com.sportbooking.modules.owner.service;

import com.sportbooking.modules.catalog.model.Court;
import com.sportbooking.shared.database.InMemoryStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Nghiệp vụ quản lý của Chủ sân (UC 3, UC 5)
 * Tương ứng modules/owner/service.ts trong anta-backend
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

    public int getBookingsCount() {
        synchronized (InMemoryStore.bookings) {
            return InMemoryStore.bookings.size();
        }
    }
}
