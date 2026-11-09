package com.sportbooking.shared.database;

import com.sportbooking.shared.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Quản lý kết nối Database (SQL Server & In-Memory fallback)
 * Tương ứng packages/database trong kiến trúc anta-backend
 */
public class DatabaseManager {
    private static boolean isSqlServerAvailable = false;

    public static void initDatabase() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                isSqlServerAvailable = true;
                System.out.println("[DATABASE] Kết nối Microsoft SQL Server THÀNH CÔNG!");
            }
        } catch (Exception e) {
            isSqlServerAvailable = false;
            System.out.println("[DATABASE] Chưa bật SQL Server -> Tự động chạy với In-Memory Dataset sẵn sàng.");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {}
        return DriverManager.getConnection(AppConfig.DB_URL, AppConfig.DB_USER, AppConfig.DB_PASSWORD);
    }

    public static boolean isSqlAvailable() {
        return isSqlServerAvailable;
    }
}
