package com.sportbooking.modules.identity.service;

import com.sportbooking.modules.identity.model.Account;
import com.sportbooking.shared.database.DatabaseManager;
import com.sportbooking.shared.database.InMemoryStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.regex.Pattern;

/**
 * Nghiệp vụ xác thực & danh tính (UC 1, UC 2, UC 12)
 * Tương ứng modules/identity/service.ts trong anta-backend
 */
public class IdentityService {

    public static class AuthResult {
        public boolean success;
        public String message;
        public Account account;

        public AuthResult(boolean success, String message, Account account) {
            this.success = success;
            this.message = message;
            this.account = account;
        }
    }

    public AuthResult register(String name, String phone, String email, String pass) {
        if (name == null || name.trim().isEmpty()) {
            return new AuthResult(false, "Nhập họ và tên", null);
        }
        if (phone == null || !Pattern.matches("^(0)(3|5|7|8|9)[0-9]{8}$", phone)) {
            return new AuthResult(false, "Số điện thoại di động không đúng", null);
        }
        if (email == null || !email.contains("@")) {
            return new AuthResult(false, "Email không hợp lệ, vui lòng nhập lại", null);
        }
        if (pass == null || pass.length() < 8) {
            return new AuthResult(false, "Mật khẩu có độ dài tối thiểu 8 ký tự", null);
        }

        synchronized (InMemoryStore.accounts) {
            for (Account a : InMemoryStore.accounts) {
                if (a.email.equalsIgnoreCase(email) || a.phone.equals(phone)) {
                    return new AuthResult(false, "Số điện thoại hoặc Email đã tồn tại", null);
                }
            }

            String newId = "TK" + String.format("%03d", InMemoryStore.accounts.size() + 1);
            Account newAcc = new Account(newId, name, phone, email, pass, "Khách hàng", "Hoạt động");
            InMemoryStore.accounts.add(newAcc);

            if (DatabaseManager.isSqlAvailable()) {
                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement ps = conn.prepareStatement("INSERT INTO TAIKHOAN(HoVaTen, SoDienThoai, Email, MatKhau, VaiTro) VALUES(?,?,?,?,?)")) {
                    ps.setNString(1, name);
                    ps.setString(2, phone);
                    ps.setString(3, email);
                    ps.setString(4, pass);
                    ps.setNString(5, "Khách hàng");
                    ps.executeUpdate();
                } catch (Exception ignored) {}
            }

            return new AuthResult(true, "Đăng ký thành công", newAcc);
        }
    }

    public AuthResult login(String identifier, String password) {
        if (identifier == null || password == null) {
            return new AuthResult(false, "Vui lòng nhập tài khoản và mật khẩu", null);
        }

        synchronized (InMemoryStore.accounts) {
            for (Account a : InMemoryStore.accounts) {
                if ((a.email.equalsIgnoreCase(identifier) || a.phone.equals(identifier)) && a.password.equals(password)) {
                    return new AuthResult(true, "Đăng nhập thành công", a);
                }
            }
        }
        return new AuthResult(false, "Đăng nhập thất bại, vui lòng thử lại", null);
    }

    public boolean updateProfile(String id, String newName) {
        if (id == null || newName == null) return false;
        synchronized (InMemoryStore.accounts) {
            for (Account a : InMemoryStore.accounts) {
                if (a.id.equals(id) || a.email.equalsIgnoreCase(id)) {
                    a.name = newName;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean changePassword(String id, String oldPass, String newPass) {
        if (id == null || oldPass == null || newPass == null || newPass.length() < 8) return false;
        synchronized (InMemoryStore.accounts) {
            for (Account a : InMemoryStore.accounts) {
                if ((a.id.equals(id) || a.email.equalsIgnoreCase(id)) && a.password.equals(oldPass)) {
                    a.password = newPass;
                    return true;
                }
            }
        }
        return false;
    }
}
