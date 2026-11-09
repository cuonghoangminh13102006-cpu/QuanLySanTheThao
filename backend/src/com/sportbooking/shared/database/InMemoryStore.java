package com.sportbooking.shared.database;

import com.sportbooking.modules.identity.model.Account;
import com.sportbooking.modules.catalog.model.Court;
import com.sportbooking.modules.booking.model.BookingOrder;
import com.sportbooking.modules.payment.model.TransactionLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Kho dữ liệu In-Memory thread-safe mẫu
 * Sẵn sàng hoạt động ngay cả khi chưa bật SQL Server
 */
public class InMemoryStore {
    public static final List<Account> accounts = Collections.synchronizedList(new ArrayList<>());
    public static final List<Court> courts = Collections.synchronizedList(new ArrayList<>());
    public static final List<BookingOrder> bookings = Collections.synchronizedList(new ArrayList<>());
    public static final List<TransactionLog> transactions = Collections.synchronizedList(new ArrayList<>());

    static {
        initSeedData();
    }

    public static void initSeedData() {
        if (accounts.isEmpty()) {
            accounts.add(new Account("TK001", "Quản trị viên", "0901234567", "admin", "1234", "Admin", "Hoạt động"));
            accounts.add(new Account("TK002", "Nguyễn Hoàng Anh", "0912345678", "chusan@gmail.com", "123456", "Chủ sân", "Hoạt động"));
            accounts.add(new Account("TK003", "Trần Văn Khách", "0923456789", "khachhang1@gmail.com", "123456", "Khách hàng", "Hoạt động"));
            accounts.add(new Account("TK004", "Lê Thị Hoa", "0934567890", "khachhang2@gmail.com", "123456", "Khách hàng", "Hoạt động"));
        }

        if (courts.isEmpty()) {
            courts.add(new Court("S001", "Sân bóng đá 7 người A", "Cơ sở Thảo Điền", "32 Quốc Hương, TP. Thủ Đức", "Bóng đá 7 người", 520000, "06:00", "22:00", "Đang hoạt động", true, "TK002", "Nguyễn Hoàng Anh", "Sân cỏ nhân tạo FIFA, đèn LED"));
            courts.add(new Court("S002", "Sân cầu lông B2", "Cơ sở Thảo Điền", "32 Quốc Hương, TP. Thủ Đức", "Cầu lông", 150000, "06:00", "22:00", "Đang hoạt động", true, "TK002", "Nguyễn Hoàng Anh", "Sân thảm PVC chống trơn"));
            courts.add(new Court("S003", "Sân bóng rổ C1", "Cơ sở Phú Nhuận", "18 Hoa Sứ, Phú Nhuận", "Bóng rổ", 260000, "06:00", "22:00", "Đang hoạt động", true, "TK002", "Nguyễn Hoàng Anh", "Sàn gỗ trong nhà"));
            courts.add(new Court("S004", "Sân bóng đá 5 người D1", "Cơ sở Bình Thạnh", "45 Điện Biên Phủ, Bình Thạnh", "Bóng đá 5 người", 340000, "06:00", "22:00", "Đang hoạt động", false, "TK002", "Nguyễn Hoàng Anh", "Sân cỏ mới"));
            courts.add(new Court("S005", "Sân Cầu lông 1 (Anta Arena)", "Anta Arena Thủ Đức", "12 Đường Số 8, P. Linh Trung, TP. Thủ Đức", "Cầu lông", 130000, "06:00", "23:00", "Đang hoạt động", true, "TK002", "Nguyễn Minh Tâm", "Thảm Yonex thi đấu"));
            courts.add(new Court("S006", "Sân Cầu lông 2 (Anta Arena)", "Anta Arena Thủ Đức", "12 Đường Số 8, P. Linh Trung, TP. Thủ Đức", "Cầu lông", 130000, "06:00", "23:00", "Đang hoạt động", true, "TK002", "Nguyễn Minh Tâm", "Thảm Yonex thi đấu"));
            courts.add(new Court("S007", "Sân Cầu lông 3 (Anta Arena)", "Anta Arena Thủ Đức", "12 Đường Số 8, P. Linh Trung, TP. Thủ Đức", "Cầu lông", 130000, "06:00", "23:00", "Đang hoạt động", true, "TK002", "Nguyễn Minh Tâm", "Thảm Yonex thi đấu"));
            courts.add(new Court("S008", "Sân Cầu lông 4 (Anta Arena)", "Anta Arena Thủ Đức", "12 Đường Số 8, P. Linh Trung, TP. Thủ Đức", "Cầu lông", 130000, "06:00", "23:00", "Đang hoạt động", true, "TK002", "Nguyễn Minh Tâm", "Sàn gỗ chuyên dụng"));
            courts.add(new Court("S009", "Sân Pickleball 1 (Anta Arena)", "Anta Arena Thủ Đức", "12 Đường Số 8, P. Linh Trung, TP. Thủ Đức", "Pickleball", 160000, "06:00", "23:00", "Đang hoạt động", true, "TK002", "Nguyễn Minh Tâm", "Sơn Acrylic 8 lớp"));
            courts.add(new Court("S010", "Sân Pickleball 2 (Anta Arena)", "Anta Arena Thủ Đức", "12 Đường Số 8, P. Linh Trung, TP. Thủ Đức", "Pickleball", 160000, "06:00", "23:00", "Đang hoạt động", true, "TK002", "Nguyễn Minh Tâm", "Sơn Acrylic 8 lớp"));
        }

        if (bookings.isEmpty()) {
            bookings.add(new BookingOrder("D001", "TK003", "Trần Văn Khách", "S001", "2026-11-10", "18:00", "19:00", 520000, 0, 100000, "Chờ xử lý", ""));
            bookings.add(new BookingOrder("D002", "TK004", "Lê Thị Hoa", "S001", "2026-11-10", "19:00", "20:00", 520000, 40000, 200000, "Đã xác nhận", ""));
            bookings.add(new BookingOrder("D003", "TK003", "Trần Văn Khách", "S002", "2026-11-10", "17:00", "18:00", 150000, 0, 0, "Đã xác nhận", ""));
            bookings.add(new BookingOrder("AN-1510-0142", "TK003", "Lê Hoàng Long", "S006", "2026-10-15", "17:00", "19:00", 300000, 100000, 300000, "Chờ xử lý", "Khách xin thêm 1 rổ cầu"));
            bookings.add(new BookingOrder("AN-1510-0141", "TK004", "Nguyễn Văn Nam", "S005", "2026-10-15", "18:00", "20:00", 240000, 0, 240000, "Đã xác nhận", ""));
            bookings.add(new BookingOrder("AN-1410-0118", "TK003", "Đặng Quốc Tuấn", "S001", "2026-10-14", "18:00", "20:00", 320000, 0, 320000, "Sự cố", "Sân mất điện"));
        }

        if (transactions.isEmpty()) {
            transactions.add(new TransactionLog("G001", "D001", "Thu tiền", 100000, "Chuyển khoản QR", "2026-11-10", "Nguyễn Hoàng Anh"));
            transactions.add(new TransactionLog("G002", "D002", "Thu tiền", 200000, "Chuyển khoản QR", "2026-11-10", "Nguyễn Hoàng Anh"));
        }
    }
}
