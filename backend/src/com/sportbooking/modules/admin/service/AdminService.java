package com.sportbooking.modules.admin.service;

import com.sportbooking.modules.identity.model.Account;
import com.sportbooking.shared.database.InMemoryStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Nghiệp vụ quản trị hệ thống (UC 4, UC 7)
 * Tương ứng modules/admin/service.ts trong anta-backend
 */
public class AdminService {

    public List<Account> getAllUsers() {
        synchronized (InMemoryStore.accounts) {
            return new ArrayList<>(InMemoryStore.accounts);
        }
    }

    public Account createUser(String name, String email, String phone, String password, String role) {
        synchronized (InMemoryStore.accounts) {
            String id = "TK" + String.format("%03d", InMemoryStore.accounts.size() + 1);
            String pass = (password != null && !password.isEmpty()) ? password : "12345678";
            String r = (role != null && !role.isEmpty()) ? role : "Chủ sân";
            Account acc = new Account(id, name, phone, email, pass, r, "Hoạt động");
            InMemoryStore.accounts.add(acc);
            return acc;
        }
    }

    public int getCourtsCount() {
        synchronized (InMemoryStore.courts) {
            return InMemoryStore.courts.size();
        }
    }

    public int getBookingsCount() {
        synchronized (InMemoryStore.bookings) {
            return InMemoryStore.bookings.size();
        }
    }
}
