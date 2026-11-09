/**
 * HỆ THỐNG QUẢN LÝ SÂN THỂ THAO
 * (Lớp chuyển tiếp tương thích ngược - Backward Compatibility)
 * 
 * Toàn bộ backend đã được tái cấu trúc thành Modular Monolith theo chuẩn Anta Backend tại:
 *   backend/src/com/sportbooking/
 * 
 * Cách chạy:
 * 1. Chạy file `run.bat` hoặc PowerShell `.\run.ps1`
 * 2. Hoặc: java -cp bin com.sportbooking.Main
 */
public class Server {
    public static void main(String[] args) throws Exception {
        com.sportbooking.Main.main(args);
    }
}
