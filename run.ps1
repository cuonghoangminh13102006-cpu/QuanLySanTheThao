# Script khởi chạy hệ thống SportBooking Fullstack
$ErrorActionPreference = "Stop"
Write-Host "==============================================================" -ForegroundColor Cyan
Write-Host "      SPORTBOOKING - HỆ THỐNG QUẢN LÝ SÂN THỂ THAO ANTA" -ForegroundColor Green
Write-Host "==============================================================" -ForegroundColor Cyan

$backendDir = Join-Path $PSScriptRoot "backend"
Set-Location $backendDir

if (-not (Test-Path "bin\com\sportbooking\Main.class")) {
    Write-Host "[*] Đang biên dịch mã nguồn Java Backend..." -ForegroundColor Yellow
    if (-not (Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }
    javac -encoding UTF-8 -sourcepath src -d bin (Join-Path "src" "com\sportbooking\Main.java")
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[LỖI] Quá trình biên dịch javac thất bại!" -ForegroundColor Red
        pause
        exit $LASTEXITCODE
    }
}

Write-Host "[*] Khởi động Fullstack Server trên Port 8080..." -ForegroundColor Green
Write-Host "-> Khách hàng : http://localhost:8080/" -ForegroundColor Cyan
Write-Host "-> Chủ sân    : http://localhost:8080/owner" -ForegroundColor Yellow
Write-Host "-> Admin      : http://localhost:8080/admin" -ForegroundColor Magenta
Write-Host "==============================================================" -ForegroundColor Cyan

java -cp bin com.sportbooking.Main
