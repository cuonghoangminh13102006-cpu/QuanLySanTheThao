# SPORTBOOKING - MODULAR BACKEND

Kiến trúc backend của dự án đã được tái cấu trúc hoàn toàn dựa theo mô hình **Modular Monolith** chuẩn hóa từ dự án `anta-backend`.

---

## 1. Cấu trúc thư mục mới (Chuẩn Anta Backend)

```text
backend/
├── src/
│   └── com/sportbooking/
│       ├── Main.java                         # Bootstrap & Route Registrar (tương đương apps/api)
│       │
│       ├── shared/                           # Các gói tiện ích chung (tương đương packages/)
│       │   ├── config/
│       │   │   └── AppConfig.java            # Cấu hình cổng 8080, DB connection
│       │   ├── database/
│       │   │   ├── DatabaseManager.java      # Quản lý kết nối SQL Server & trạng thái
│       │   │   └── InMemoryStore.java        # Kho dữ liệu thread-safe in-memory sẵn sàng
│       │   └── http/
│       │       └── HttpUtils.java            # Tiện ích CORS, JSON parser/serializer, readBody
│       │
│       └── modules/                          # Các miền nghiệp vụ độc lập (tương đương modules/)
│           ├── identity/                     # UC 1, UC 2, UC 12: Đăng ký, Đăng nhập, Hồ sơ, Đổi mật khẩu
│           │   ├── model/Account.java
│           │   ├── service/IdentityService.java
│           │   └── controller/
│           │       ├── AuthRegisterHandler.java
│           │       ├── AuthLoginHandler.java
│           │       ├── AuthProfileHandler.java
│           │       └── AuthChangePasswordHandler.java
│           │
│           ├── catalog/                      # UC 8: Danh mục & Xem sân công khai
│           │   ├── model/Court.java
│           │   ├── service/CatalogService.java
│           │   └── controller/CatalogCourtsHandler.java
│           │
│           ├── booking/                      # UC 9, UC 10, UC 11: Đặt sân & Theo dõi đơn của khách
│           │   ├── model/BookingOrder.java
│           │   ├── service/BookingService.java
│           │   └── controller/BookingHandler.java
│           │
│           ├── owner/                        # UC 3, UC 5: Quản lý sân, lịch & đơn của Chủ sân
│           │   ├── service/OwnerService.java
│           │   └── controller/
│           │       ├── OwnerCourtsHandler.java
│           │       └── OwnerBookingsHandler.java
│           │
│           ├── payment/                      # UC 6: Thanh toán & Báo cáo doanh thu
│           │   ├── model/TransactionLog.java
│           │   ├── service/PaymentService.java
│           │   └── controller/
│           │       ├── PaymentHandler.java
│           │       └── ReportHandler.java
│           │
│           └── admin/                        # UC 4, UC 7: Quản lý tài khoản & Giám sát hệ thống
│               ├── service/AdminService.java
│               └── controller/
│                   ├── AdminUsersHandler.java
│                   ├── AdminCourtsHandler.java
│                   └── AdminBookingsHandler.java
│
├── legacy/                                   # Mã nguồn cũ được sao lưu nguyên vẹn
│   ├── Server.java
│   └── login.java
│
├── bin/                                      # Thư mục bytecode sau khi build (*.class)
├── run.bat                                   # Script chạy nhanh 1-click trên Windows Command Prompt
├── run.ps1                                   # Script chạy trên Windows PowerShell
└── Server.java                               # Lớp bọc tương thích ngược
```

---

## 2. Cách khởi động Backend

### Cách 1: Chạy file script tự động (Khuyên dùng)
* Nhấp đúp vào file `run.bat` (Command Prompt).
* Hoặc mở PowerShell tại thư mục `backend` và chạy:
  ```powershell
  .\run.ps1
  ```

### Cách 2: Biên dịch và chạy bằng lệnh Java thuần
```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Path src -Recurse -Filter *.java).FullName
java -cp bin com.sportbooking.Main
```

---

## 3. Danh sách Endpoints API

| Module | Phương thức | Endpoint | Chức năng (Use Case) |
| :--- | :--- | :--- | :--- |
| **Identity** | `POST` | `/api/auth/register` (hoặc `/api/register`) | UC 1: Đăng ký tài khoản |
| **Identity** | `POST` | `/api/auth/login` (hoặc `/api/login`) | UC 2: Đăng nhập |
| **Identity** | `PUT` | `/api/auth/profile` | UC 12: Đổi tên / hồ sơ |
| **Identity** | `POST` | `/api/auth/change-password` | UC 12: Đổi mật khẩu |
| **Catalog** | `GET` | `/api/courts` | UC 8: Danh mục sân công khai |
| **Booking** | `POST` | `/api/bookings` | UC 9: Khách tạo đơn đặt sân |
| **Booking** | `GET` | `/api/bookings` | UC 10, 11: Lịch sử đơn đặt |
| **Owner** | `GET, POST`| `/api/owner/courts` | UC 3: Chủ sân xem và thêm sân |
| **Owner** | `GET` | `/api/owner/bookings` | UC 5: Chủ sân quản lý đơn đặt |
| **Payment** | `GET` | `/api/owner/payments` | UC 6: Quản lý giao dịch thu tiền |
| **Payment** | `GET` | `/api/owner/reports` | UC 6: Báo cáo doanh thu & tỷ lệ lấp đầy |
| **Admin** | `GET, POST`| `/api/admin/users` | UC 4: Quản lý tài khoản người dùng |
| **Admin** | `GET` | `/api/admin/courts` | UC 7: Giám sát toàn bộ sân thể thao |
| **Admin** | `GET` | `/api/admin/bookings` | UC 7: Giám sát đơn đặt toàn hệ thống |
