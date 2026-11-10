@echo off
title SportBooking Backend Server
echo [SportBooking] Dang bien dich ma nguon Modular Backend...
if not exist "bin" mkdir bin
javac -encoding UTF-8 -sourcepath src -d bin src\com\sportbooking\Main.java

if %ERRORLEVEL% equ 0 (
    echo [SportBooking] Bien dich thanh cong! Dang khoi dong Server...
    java -cp bin com.sportbooking.Main
) else (
    echo [LOI] Qua trinh bien dich that bai!
    pause
)
