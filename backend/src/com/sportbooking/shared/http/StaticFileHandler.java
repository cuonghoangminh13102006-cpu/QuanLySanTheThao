package com.sportbooking.shared.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;

/**
 * Phục vụ tĩnh toàn bộ Frontend (HTML, CSS, JS, Assets)
 * Tương tự app.use('/admin', express.static(...)) và app.use('/owner', ...) trong anta-backend
 */
public class StaticFileHandler implements HttpHandler {
    private final File baseDir;

    public StaticFileHandler() {
        // Tìm kiếm thư mục frontend
        File candidate1 = new File("../frontend");
        File candidate2 = new File("frontend");
        File candidate3 = new File("D:/hh/QuanLySanTheThao-main/QuanLySanTheThao-main/frontend");

        if (candidate1.exists() && candidate1.isDirectory()) {
            this.baseDir = candidate1;
        } else if (candidate2.exists() && candidate2.isDirectory()) {
            this.baseDir = candidate2;
        } else {
            this.baseDir = candidate3;
        }
        System.out.println("[STATIC] Thư mục Frontend được liên kết tại: " + this.baseDir.getAbsolutePath());
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        HttpUtils.enableCORS(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        URI uri = ex.getRequestURI();
        String path = uri.getPath();

        // Không xử lý route /api/ ở static handler
        if (path.startsWith("/api/")) {
            ex.sendResponseHeaders(404, -1);
            return;
        }

        // Điều hướng thông minh /admin và /owner (tương tự Anta Backend)
        if (path.equals("/admin") || path.equals("/admin/")) {
            ex.getResponseHeaders().set("Location", "/admin.html");
            ex.sendResponseHeaders(302, -1);
            return;
        }
        if (path.equals("/owner") || path.equals("/owner/")) {
            ex.getResponseHeaders().set("Location", "/owner.html");
            ex.sendResponseHeaders(302, -1);
            return;
        }

        // Mặc định root chuyển về index.html
        if (path.equals("/") || path.isEmpty()) {
            path = "/index.html";
        }

        // Chuẩn hóa đường dẫn an toàn
        File targetFile = new File(baseDir, path.replace("/", File.separator));

        if (!targetFile.exists() || targetFile.isDirectory()) {
            // Thử kiểm tra nếu là file .html bị thiếu đuôi
            File htmlFile = new File(baseDir, path.replace("/", File.separator) + ".html");
            if (htmlFile.exists() && !htmlFile.isDirectory()) {
                targetFile = htmlFile;
            } else {
                String notFoundMsg = "404 Not Found: " + path;
                ex.sendResponseHeaders(404, notFoundMsg.length());
                try (OutputStream os = ex.getResponseBody()) {
                    os.write(notFoundMsg.getBytes());
                }
                return;
            }
        }

        String mimeType = getMimeType(targetFile.getName());
        ex.getResponseHeaders().set("Content-Type", mimeType);
        ex.sendResponseHeaders(200, targetFile.length());

        try (FileInputStream fis = new FileInputStream(targetFile);
             OutputStream os = ex.getResponseBody()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        }
    }

    private String getMimeType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".ttf")) return "font/ttf";
        if (lower.endsWith(".woff")) return "font/woff";
        if (lower.endsWith(".woff2")) return "font/woff2";
        if (lower.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}
