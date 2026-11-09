# 🏸 Anta SportBooking - Hệ Thống Quản Lý & Đặt Sân Thể Thao Trực Tuyến

> Dự án Fullstack hoàn chỉnh gồm: **Web Khách Hàng**, **Cổng Quản Lý Chủ Sân (Owner)** và **Cổng Quản Trị Hệ Thống (Super Admin)** được thiết kế theo đúng chuẩn bộ giao diện Anta Sports.

---

## 🚀 1. Hướng Dẫn Khởi Chạy Nhanh (1-Click)

Bạn chỉ cần chạy 1 tệp duy nhất để khởi động toàn bộ cả Backend và Frontend:

- **Cách 1 (Windows - Khuyến nghị)**: Bấm đúp vào tệp [`run.bat`](run.bat) ở thư mục gốc của dự án.
- **Cách 2 (PowerShell)**: Chạy lệnh `.\run.ps1` trong PowerShell.
- **Cách 3 (Thủ công qua terminal)**:
  ```bash
  cd backend
  javac -encoding UTF-8 -d bin src/com/sportbooking/**/*.java
  java -cp bin com.sportbooking.Main
  ```

Sau khi khởi chạy, mở trình duyệt tại:
- 🌐 **Web Khách Hàng**: [http://localhost:8080/](http://localhost:8080/)
- 🏸 **Cổng Chủ Sân (Anta Arena)**: [http://localhost:8080/owner](http://localhost:8080/owner) *(hoặc `/owner.html`)*
- 🛡️ **Cổng Quản Trị Hệ Thống (Super Admin)**: [http://localhost:8080/admin](http://localhost:8080/admin) *(hoặc `/admin.html`)*
- 🔑 **Trang Đăng Nhập / Phân Quyền**: [http://localhost:8080/login.html](http://localhost:8080/login.html)

---

## 👥 2. Tài Khoản Thử Nghiệm Sẵn Có (Test Credentials)

| Vai trò | Email / Tên đăng nhập | Mật khẩu | Chức năng truy cập |
| :--- | :--- | :--- | :--- |
| **🛡️ Quản trị viên (Super Admin)** | `admin` hoặc `admin@sportbooking.com` | `1234` hoặc `123456` | Quản trị toàn sàn, duyệt cơ sở, phán quyết sự cố, duyệt lệnh hoàn tiền VietQR, audit log |
| **🏸 Chủ cơ sở (Anta Arena)** | `chusan@gmail.com` | `123456` | Quản lý 6 sân đấu, lịch đặt 06:00-22:00, duyệt đơn, xử lý huỷ hoàn tiền, thu chi, kho dịch vụ |
| **👤 Khách hàng** | `khachhang1@gmail.com` | `123456` | Xem bản đồ, tìm kiếm sân, đặt sân theo khung giờ, quét mã VietQR |

*(Trên trang `login.html` đã tích hợp sẵn 3 nút bấm Đăng nhập nhanh 1-Click để chuyển vai trò tức thì mà không cần gõ phím).*

---

## 🏗️ 3. Kiến Trúc Dự Án (Modular Monolith)

Dự án được chuẩn hóa theo kiến trúc module tương đồng với `anta-backend`:

```text
QuanLySanTheThao-main/
├── run.bat                          # Script 1-click khởi chạy fullstack trên Windows
├── run.ps1                          # Script khởi chạy cho PowerShell
├── database/
│   ├── schema.sql                   # Kịch bản tạo 10 bảng CSDL Microsoft SQL Server
│   └── seeddata.sql                 # Dữ liệu mẫu chuẩn của các cơ sở & tài khoản
├── backend/                         # Backend Java Modular Engine
│   ├── src/com/sportbooking/
│   │   ├── shared/                  # Config, Database Manager, Http Utilities, Static File Handler
│   │   │   ├── config/AppConfig.java
│   │   │   ├── database/DatabaseManager.java (Hỗ trợ SQL Server & tự động In-Memory fallback)
│   │   │   ├── database/InMemoryStore.java   (Chứa sẵn dữ liệu 6 sân Anta Arena & các đơn đặt)
│   │   │   └── http/StaticFileHandler.java   (Phục vụ HTML/CSS/JS tĩnh)
│   │   ├── modules/
│   │   │   ├── identity/            # UC1, UC2, UC12: Đăng nhập, Đăng ký, Đổi mật khẩu
│   │   │   ├── catalog/             # UC8: Danh mục sân & cơ sở
│   │   │   ├── booking/             # UC9, UC10, UC11: Đặt sân & đơn đặt
│   │   │   ├── owner/               # UC3, UC5: Quản lý sân, lịch sân, đơn của chủ sân
│   │   │   ├── payment/             # UC6: Giao dịch & báo cáo thu chi
│   │   │   └── admin/               # UC4, UC7: Quản trị tài khoản, kiểm soát toàn sàn
│   │   └── Main.java                # Entry point bootstrap server port 8080
│   └── bin/                         # File .class đã được biên dịch sẵn
└── frontend/                        # Giao diện người dùng
    ├── css/anta-portal.css          # Design system Anta (#0A93A3, #3FAE3A, #E07A10, Be Vietnam Pro)
    ├── index.html                   # Web Đặt Sân dành cho Khách hàng
    ├── login.html                   # Trang đăng nhập / đăng ký / phân quyền
    ├── owner.html                   # Cổng Chủ Sân (Đầy đủ 8 màn hình theo Trang 3-10 PDF)
    ├── admin.html                   # Cổng Quản Trị Viên (Đầy đủ 6 màn hình theo Trang 11-16 PDF)
    ├── js/                          # Logic dữ liệu & tương tác khách hàng
    └── sportbooking-html/           # Giao diện quản lý phụ trợ
```

---

## 📡 4. Danh Sách REST API Endpoints

| Nhóm Module | Method | Đường dẫn API | Chức năng |
| :--- | :--- | :--- | :--- |
| **Identity** | `POST` | `/api/auth/login` | Xác thực đăng nhập qua Email / SĐT & mật khẩu |
| | `POST` | `/api/auth/register` | Đăng ký tài khoản người dùng mới |
| | `GET` | `/api/auth/profile` | Xem thông tin tài khoản hiện tại |
| | `POST` | `/api/auth/change-password` | Đổi mật khẩu tài khoản |
| **Catalog** | `GET` | `/api/courts` | Danh sách toàn bộ sân thể thao đang hoạt động |
| **Booking** | `GET` | `/api/bookings` | Danh sách đơn đặt sân |
| | `POST` | `/api/bookings` | Tạo đơn đặt sân mới |
| **Owner** | `GET/POST`| `/api/owner/courts` | Danh sách & Thêm mới sân con tại cơ sở |
| | `GET` | `/api/owner/bookings` | Lấy số lượng và danh sách đơn của cơ sở |
| **Payment** | `GET` | `/api/owner/payments` | Lịch sử giao dịch & dòng tiền |
| | `GET` | `/api/owner/reports` | Báo cáo doanh thu |
| **Admin** | `GET/POST`| `/api/admin/users` | Danh sách người dùng & Thêm tài khoản mới |
| | `GET` | `/api/admin/courts` | Giám sát toàn bộ sân đấu của 56 cơ sở |
| | `GET` | `/api/admin/bookings` | Toàn bộ đơn đặt và theo dõi sự cố sàn |

---

## 🗄️ 5. Cấu Hình Cơ Sở Dữ Liệu SQL Server (Tuỳ chọn)

Hệ thống được thiết kế cơ chế **Zero-Dependency Fallback**:
- Nếu máy tính đã cài đặt **Microsoft SQL Server**, mở SQL Server Management Studio (SSMS) và chạy lần lượt 2 file [`database/schema.sql`](database/schema.sql) và [`database/seeddata.sql`](database/seeddata.sql). Sau đó cập nhật thông tin đăng nhập trong [`AppConfig.java`](backend/src/com/sportbooking/shared/config/AppConfig.java).
- Nếu máy tính **chưa bật SQL Server**, hệ thống sẽ tự động chuyển sang cơ chế **In-Memory Store thread-safe** với dữ liệu mẫu hoàn chỉnh, đảm bảo hệ thống luôn luôn chạy ổn định 100% mà không bao giờ gặp lỗi gián đoạn.
