package com.sportbooking.shared.config;

/**
 * Cấu hình hệ thống (Port, Database, Environment)
 * Tương ứng với packages/config trong kiến trúc anta-backend
 */
public class AppConfig {
    public static final int PORT = 8080;
    
    // Cấu hình Microsoft SQL Server (database quan_ly_san_the_thao)
    public static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=quan_ly_san_the_thao;encrypt=true;trustServerCertificate=true;";
    public static final String DB_USER = "sa";
    public static final String DB_PASSWORD = "YourPassword123";
}
