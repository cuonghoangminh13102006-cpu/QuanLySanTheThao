--Dữ liệu mẫu--
USE quan_ly_san_the_thao;
GO

-- 1. Chèn dữ liệu mẫu TAIKHOAN (Đã cập nhật Admin theo yêu cầu)
INSERT INTO TAIKHOAN (HoVaTen, SoDienThoai, Email, MatKhau, VaiTro) VALUES
(N'Quản trị viên', '0901234567', 'admin', '1234', N'Admin'),
(N'Nguyễn Văn Chủ', '0912345678', 'chusan@gmail.com', '123456', N'Chủ sân'),
(N'Trần Văn Khách', '0923456789', 'khachhang1@gmail.com', '123456', N'Khách hàng'),
(N'Lê Thị Hoa', '0934567890', 'khachhang2@gmail.com', '123456', N'Khách hàng');

-- 2. Chèn dữ liệu mẫu COSO
INSERT INTO COSO (TenCoSo, DiaChi) VALUES
(N'Cơ sở Thể thao Cầu Giấy', N'123 Cầu Giấy, Hà Nội'),
(N'Cơ sở Thể thao Mỹ Đình', N'45 Mỹ Đình, Nam Từ Liêm, Hà Nội');

-- 3. Chèn dữ liệu mẫu LOAISAN
INSERT INTO LOAISAN (TenLoaiSan) VALUES
(N'Sân bóng đá 7 người'),
(N'Sân bóng đá 5 người'),
(N'Sân cầu lông thảm PVC');

-- 4. Chèn dữ liệu mẫu SAN
INSERT INTO SAN (TenSan, HinhAnh, GioMoCua, GioDongCua, TrangThai, DiaChi, Gia, SoDienThoai, ThongTin, DichVu, DieuKhoanVaqQuyDinh, DanhGia, MaCoSo) VALUES
(N'Sân Bóng Cầu Giấy 1', 'san_cg_1.jpg', '06:00:00', '22:00:00', N'Hoạt động', N'123 Cầu Giấy, Hà Nội', 300000, '0912345678', N'Sân cỏ nhân tạo đạt chuẩn FIFA', N'Nước uống, Thuê giày, Đèn chiếu sáng', N'Đặt cọc 50% trước khi nhận sân', 4.5, 1),
(N'Sân Cầu Lông Mỹ Đình A', 'san_cl_md.jpg', '07:00:00', '23:00:00', N'Hoạt động', N'45 Mỹ Đình, Hà Nội', 150000, '0912345678', N'Sân thảm chống trơn trượt có điều hòa', N'Cho thuê vợt, Nước giải khát', N'Đến đúng giờ đăng ký', 4.8, 2);

-- 5. Chèn dữ liệu mẫu SAN_LOAISAN (Bảng trung gian)
INSERT INTO SAN_LOAISAN (MaSan, MaLoaiSan) VALUES
(1, 1), -- Sân Cầu Giấy 1 thuộc Loại sân 7 người
(2, 3); -- Sân Mỹ Đình A thuộc Loại sân Cầu lông

-- 6. Chèn dữ liệu mẫu NHANVIEN
INSERT INTO NHANVIEN (HoVaTen, SoDienThoai, Email, TrangThaiLamViec, MaCoSo) VALUES
(N'Phạm Văn Nhân', '0945678901', 'nhanvien1@gmail.com', N'Đang làm việc', 1),
(N'Hoàng Thị Viên', '0956789012', 'nhanvien2@gmail.com', N'Đang làm việc', 2);

-- 7. Chèn dữ liệu mẫu DONDATSAN
INSERT INTO DONDATSAN (MaTaiKhoan, MaSan, NgayDat, GioBatDau, GioKetThuc, TongTien, TrangThaiDon, MaNhanVien) VALUES
(3, 1, '2026-11-01', '18:00:00', '19:30:00', 450000, N'Hoàn thành', 1),
(4, 2, '2026-11-02', '19:00:00', '20:00:00', 150000, N'Đã hủy', 2);

-- 8. Chèn dữ liệu mẫu DONHUYSAN
INSERT INTO DONHUYSAN (MaDonDatSan, ThoiDiemGui, LyDoHuy, SoTienHoanLai, TrangThaiXuLy, GhiChu, MaNhanVien) VALUES
(2, '2026-10-31 10:00:00', N'Khách bận đột xuất', 150000, N'Đã hoàn tiền', N'Đã xử lý chuyển khoản lại cho khách', 2);

-- 9. Chèn dữ liệu mẫu GIAODICH
INSERT INTO GIAODICH (MaDonDatSan, MaDonHuySan, LoaiGiaoDich, SoTien, PhuongThucThanhToan, ThoiDiemGiaoDich, TrangThaiGiaoDich) VALUES
(1, NULL, N'Thanh toán đặt sân', 450000, N'Chuyển khoản QR', '2026-10-30 14:20:00', N'Thành công'),
(2, 1, N'Hoàn tiền đơn hủy', 150000, N'Chuyển khoản ngân hàng', '2026-10-31 10:30:00', N'Thành công');