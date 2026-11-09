@echo off
chcp 65001 >nul
echo ==============================================================
echo       SPORTBOOKING - HỆ THỐNG QUẢN LÝ SÂN THỂ THAO ANTA
echo ==============================================================
echo.

cd /d "%~dp0\backend"

echo [1/3] Đang kiểm tra mã nguồn Java Backend...
if not exist "bin\com\sportbooking\Main.class" (
    echo [2/3] Chưa có file biên dịch, đang tiến hành biên dịch javac...
    if not exist "bin" mkdir bin
    dir /s /b src\*.java > sources.txt
    javac -encoding UTF-8 -d bin @sources.txt
    del sources.txt
) else (
    echo [2/3] Đã có bản biên dịch sẵn sàng.
)

echo [3/3] Đang khởi động Fullstack Server trên cổng 8080...
echo.
echo -> Mở trình duyệt tại: http://localhost:8080/
echo -> Cổng Chủ Sân (Anta)    : http://localhost:8080/owner
echo -> Cổng Quản Trị Sàn (Ad) : http://localhost:8080/admin
echo.
echo (Nhấn Ctrl + C để dừng máy chủ)
echo ==============================================================
java -cp bin com.sportbooking.Main
pause
