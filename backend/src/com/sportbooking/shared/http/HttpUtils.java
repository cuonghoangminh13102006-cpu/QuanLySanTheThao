package com.sportbooking.shared.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tiện ích HTTP & JSON envelope
 * Tương ứng packages/observability/http trong kiến trúc anta-backend
 */
public class HttpUtils {

    public static void enableCORS(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, Idempotency-Key");
    }

    public static String readBody(HttpExchange ex) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    public static void sendJson(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static String getField(String json, String field) {
        return getField(json, field, "");
    }

    public static String getField(String json, String field, String def) {
        if (json == null || json.isEmpty()) return def;

        // String field: "field": "value"
        String strPattern = "\"" + field + "\"\\s*:\\s*\"([^\"]*)\"";
        Matcher m1 = Pattern.compile(strPattern).matcher(json);
        if (m1.find()) return m1.group(1);

        // Numeric/Boolean field: "field": 123 / "field": true
        String numPattern = "\"" + field + "\"\\s*:\\s*([0-9a-zA-Z._-]+)";
        Matcher m2 = Pattern.compile(numPattern).matcher(json);
        if (m2.find()) return m2.group(1);

        return def;
    }

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
