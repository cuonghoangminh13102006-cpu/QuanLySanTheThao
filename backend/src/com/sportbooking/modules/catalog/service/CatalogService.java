package com.sportbooking.modules.catalog.service;

import com.sportbooking.modules.catalog.model.Court;
import com.sportbooking.shared.database.InMemoryStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Nghiệp vụ danh mục sân (UC 8)
 * Tương ứng modules/catalog/service.ts trong anta-backend
 */
public class CatalogService {

    public List<Court> getPublicCourts() {
        List<Court> result = new ArrayList<>();
        synchronized (InMemoryStore.courts) {
            for (Court c : InMemoryStore.courts) {
                if (c.visible) {
                    result.add(c);
                }
            }
        }
        return result;
    }

    public List<Court> getAllCourts() {
        synchronized (InMemoryStore.courts) {
            return new ArrayList<>(InMemoryStore.courts);
        }
    }

    public Court findById(String id) {
        synchronized (InMemoryStore.courts) {
            for (Court c : InMemoryStore.courts) {
                if (c.id.equalsIgnoreCase(id)) return c;
            }
        }
        return null;
    }
}
