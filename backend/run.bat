@echo off
chcp 65001 >nul
echo [SportBooking] Đang biên dịch mã nguồn Modular Monolith...
if not exist "bin" mkdir bin
javac -encoding UTF-8 -d bin src\com\sportbooking\shared\config\*.java src\com\sportbooking\shared\http\*.java src\com\sportbooking\modules\identity\model\*.java src\com\sportbooking\modules\catalog\model\*.java src\com\sportbooking\modules\booking\model\*.java src\com\sportbooking\modules\payment\model\*.java src\com\sportbooking\shared\database\*.java src\com\sportbooking\modules\identity\service\*.java src\com\sportbooking\modules\catalog\service\*.java src\com\sportbooking\modules\booking\service\*.java src\com\sportbooking\modules\owner\service\*.java src\com\sportbooking\modules\payment\service\*.java src\com\sportbooking\modules\admin\service\*.java src\com\sportbooking\modules\identity\controller\*.java src\com\sportbooking\modules\catalog\controller\*.java src\com\sportbooking\modules\booking\controller\*.java src\com\sportbooking\modules\owner\controller\*.java src\com\sportbooking\modules\payment\controller\*.java src\com\sportbooking\modules\admin\controller\*.java src\com\sportbooking\Main.java

if %ERRORLEVEL% equ 0 (
    echo [SportBooking] Biên dịch thành công! Đang khởi động Server...
    java -cp bin com.sportbooking.Main
) else (
    echo [LỖI] Quá trình biên dịch thất bại!
    pause
)
