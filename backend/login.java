/**
 * HỆ THỐNG QUẢN LÝ SÂN THỂ THAO
 * (Lớp chuyển tiếp tương thích ngược - Backward Compatibility)
 * 
 * Toàn bộ logic Đăng ký & Đăng nhập đã được module hóa tại:
 *   backend/src/com/sportbooking/modules/identity/
 * 
 * File gốc ban đầu được sao lưu tại: backend/legacy/login.java
 */
public class login {
    public static void main(String[] args) throws Exception {
        System.out.println("[INFO] Module Identity đã được hợp nhất vào hệ thống Modular Monolith.");
        System.out.println("[INFO] Đang khởi động toàn bộ hệ thống API...");
        com.sportbooking.Main.main(args);
    }
}
