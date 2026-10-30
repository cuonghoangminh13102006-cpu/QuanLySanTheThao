-- Tạo Database (nếu chưa có) và sử dụng
CREATE DATABASE IF NOT EXISTS quan_ly_san_the_thao
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quan_ly_san_the_thao;

-- 1. Bảng TAIKHOAN
CREATE TABLE TAIKHOAN (
    MaTaiKhoan INT AUTO_INCREMENT PRIMARY KEY,
    HoVaTen VARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(20),
    Email VARCHAR(100) NOT NULL UNIQUE,
    MatKhau VARCHAR(255) NOT NULL,
    VaiTro VARCHAR(50) NOT NULL -- Ví dụ: Khách hàng, Chủ sân, Admin
);

-- 2. Bảng COSO
CREATE TABLE COSO (
    MaCoSo INT AUTO_INCREMENT PRIMARY KEY,
    TenCoSo VARCHAR(150) NOT NULL,
    DiaChi VARCHAR(255) NOT NULL
);

-- 3. Bảng LOAISAN
CREATE TABLE LOAISAN (
    MaLoaiSan INT AUTO_INCREMENT PRIMARY KEY,
    TenLoaiSan VARCHAR(100) NOT NULL -- Ví dụ: Sân 5 người, Sân 7 người, Sân thảm...
);

-- 4. Bảng SAN
CREATE TABLE SAN (
    MaSan INT AUTO_INCREMENT PRIMARY KEY,
    TenSan VARCHAR(150) NOT NULL,
    HinhAnh VARCHAR(255),
    GioMoCua TIME,
    GioDongCua TIME,
    TrangThai VARCHAR(50),
    DiaChi VARCHAR(255),
    Gia DECIMAL(12, 2) NOT NULL,
    SoDienThoai VARCHAR(20),
    ThongTin TEXT,
    DichVu TEXT,
    DieuKhoanVaqQuyDinh TEXT,
    DanhGia DECIMAL(3, 2) DEFAULT 0.0,
    MaCoSo INT NOT NULL,
    FOREIGN KEY (MaCoSo) REFERENCES COSO(MaCoSo) ON DELETE CASCADE
);

-- 5. Bảng trung gian SAN_LOAISAN (Giải quyết quan hệ Nhiều - Nhiều giữa SAN và LOAISAN)
CREATE TABLE SAN_LOAISAN (
    MaSan INT NOT NULL,
    MaLoaiSan INT NOT NULL,
    PRIMARY KEY (MaSan, MaLoaiSan),
    FOREIGN KEY (MaSan) REFERENCES SAN(MaSan) ON DELETE CASCADE,
    FOREIGN KEY (MaLoaiSan) REFERENCES LOAISAN(MaLoaiSan) ON DELETE CASCADE
);

-- 6. Bảng NHANVIEN
CREATE TABLE NHANVIEN (
    MaNhanVien INT AUTO_INCREMENT PRIMARY KEY,
    HoVaTen VARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(20),
    Email VARCHAR(100) NOT NULL UNIQUE,
    TrangThaiLamViec VARCHAR(50),
    MaCoSo INT NOT NULL,
    FOREIGN KEY (MaCoSo) REFERENCES COSO(MaCoSo) ON DELETE CASCADE
);

-- 7. Bảng DONDATSAN
CREATE TABLE DONDATSAN (
    MaDonDatSan INT AUTO_INCREMENT PRIMARY KEY,
    MaTaiKhoan INT NOT NULL,
    MaSan INT NOT NULL,
    NgayDat DATE NOT NULL,
    GioBatDau TIME NOT NULL,
    GioKetThuc TIME NOT NULL,
    TongTien DECIMAL(12, 2) NOT NULL,
    TrangThaiDon VARCHAR(50) NOT NULL, -- Ví dụ: Chờ xác nhận, Đã đặt, Hoàn thành
    MaNhanVien INT NOT NULL,
    FOREIGN KEY (MaTaiKhoan) REFERENCES TAIKHOAN(MaTaiKhoan),
    FOREIGN KEY (MaSan) REFERENCES SAN(MaSan),
    FOREIGN KEY (MaNhanVien) REFERENCES NHANVIEN(MaNhanVien)
);

-- 8. Bảng DONHUYSAN
CREATE TABLE DONHUYSAN (
    MaDonHuySan INT AUTO_INCREMENT PRIMARY KEY,
    MaDonDatSan INT NOT NULL,
    ThoiDiemGui DATETIME DEFAULT CURRENT_TIMESTAMP,
    LyDoHuy TEXT,
    SoTienHoanLai DECIMAL(12, 2) DEFAULT 0,
    TrangThaiXuLy VARCHAR(50), -- Ví dụ: Chờ xử lý, Đã hoàn tiền, Từ chối
    GhiChu TEXT,
    MaNhanVien INT NOT NULL,
    FOREIGN KEY (MaDonDatSan) REFERENCES DONDATSAN(MaDonDatSan),
    FOREIGN KEY (MaNhanVien) REFERENCES NHANVIEN(MaNhanVien)
);

-- 9. Bảng GIAODICH
CREATE TABLE GIAODICH (
    MaGiaoDich INT AUTO_INCREMENT PRIMARY KEY,
    MaDonDatSan INT NOT NULL,
    MaDonHuySan INT NULL, -- Có thể NULL nếu là giao dịch thanh toán đặt sân thông thường
    LoaiGiaoDich VARCHAR(50) NOT NULL, -- Ví dụ: Thanh toán, Hoàn tiền
    SoTien DECIMAL(12, 2) NOT NULL,
    PhuongThucThanhToan VARCHAR(50), -- Ví dụ: Chuyển khoản, Tiền mặt, Ví điện tử
    ThoiDiemGiaoDich DATETIME DEFAULT CURRENT_TIMESTAMP,
    TrangThaiGiaoDich VARCHAR(50) NOT NULL, -- Ví dụ: Thành công, Thất bại, Đang xử lý
    FOREIGN KEY (MaDonDatSan) REFERENCES DONDATSAN(MaDonDatSan),
    FOREIGN KEY (MaDonHuySan) REFERENCES DONHUYSAN(MaDonHuySan)
);