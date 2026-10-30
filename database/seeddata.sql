USE quan_ly_san_the_thao;
GO

-- 1. Chèn dữ liệu mẫu TAIKHOAN
INSERT INTO TAIKHOAN (HoVaTen, SoDienThoai, Email, MatKhau, VaiTro) VALUES
(N'Quản trị viên', '0901234567', 'admin@gmail.com', '123456', N'Admin'),
(N'Chủ sân Nguyễn Văn A', '0912345678', 'chusan@gmail.com', '123456', N'Chủ sân'),
(N'Khách hàng Trần Văn B', '0923456789', 'khachhang@gmail.com', '123456', N'Khách hàng');

-- 2. Chèn dữ liệu mẫu COSO
INSERT INTO COSO (TenCoSo, DiaChi) VALUES
(N'Cơ sở Cầu Giấy', N'123 Cầu Giấy, Hà Nội'),
(N'Cơ sở Mỹ Đình', N'45 Mỹ Đình, Hà Nội');

-- 3. Chèn dữ liệu mẫu LOAISAN
INSERT INTO LOAISAN (TenLoaiSan) VALUES
(N'Sân bóng đá 7 người'),
(N'Sân cầu lông thảm');

-- 4. Chèn dữ liệu mẫu SAN (Gắn với Cơ sở 1)
INSERT INTO SAN (TenSan, Gia, DiaChi, MaCoSo, SoDienThoai) VALUES
(N'Sân bóng Cầu Giấy 1', 300000, N'123 Cầu Giấy, Hà Nội', 1, '0912345678'),
(N'Sân Cầu Lông Mỹ Đình 1', 150000, N'45 Mỹ Đình, Hà Nội', 2, '0912345678');