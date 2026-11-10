@echo off
title SportBooking Fullstack Server
cd /d "%~dp0backend"

echo ==============================================================
echo       SPORTBOOKING - HE THONG QUAN LY SAN THE THAO ANTA
echo ==============================================================
echo.

echo [1/3] Kiem tra ma nguon Java Backend...
if not exist "bin\com\sportbooking\Main.class" (
    echo [2/3] Dang bien dich javac backend...
    if not exist "bin" mkdir bin
    javac -encoding UTF-8 -sourcepath src -d bin src\com\sportbooking\Main.java
    if errorlevel 1 (
        echo.
        echo [LOI] Bien dich javac that bai! Vui long kiem tra lai Java JDK.
        pause
        exit /b 1
    )
) else (
    echo [2/3] Da co ban bien dich san sang.
)

echo [3/3] Dang khoi dong Fullstack Server tren cong 8080...
echo.
echo -^> Khach hang dat san : http://localhost:8080/
echo -^> Cong Chu San       : http://localhost:8080/owner
echo -^> Cong Quan Tri Vien : http://localhost:8080/admin
echo.
echo Nhap Ctrl + C de dung may chu.
echo ==============================================================
java -cp bin com.sportbooking.Main
pause
