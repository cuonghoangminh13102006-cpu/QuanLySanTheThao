package com.sportbooking.modules.identity.model;

/**
 * Model Tài khoản người dùng (Khách hàng, Chủ sân, Admin)
 * Tương ứng bảng TAIKHOAN và identity.users trong anta-backend
 */
public class Account {
    public String id;
    public String name;
    public String phone;
    public String email;
    public String password;
    public String role;
    public String status;

    public Account(String id, String name, String phone, String email, String password, String role, String status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
    }
}
