# Chạy backend theo kiến trúc Modular Monolith chuẩn hóa
$OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "[SportBooking] Đang quét và biên dịch mã nguồn Modular Backend..." -ForegroundColor Cyan
if (!(Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sourceFiles = (Get-ChildItem -Path "src" -Recurse -Filter "*.java").FullName
javac -encoding UTF-8 -d bin $sourceFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "[SportBooking] Biên dịch thành công! Khởi động HTTP Server..." -ForegroundColor Green
    java -cp bin com.sportbooking.Main
} else {
    Write-Host "[LỖI] Biên dịch thất bại!" -ForegroundColor Red
}
