package com.groupsapp.monolito.dto;

import java.time.LocalDateTime;

// Wrapper genérico que envuelve TODAS las respuestas de la API
// Así el cliente siempre recibe el mismo formato JSON:
// {
//   "success": true,
//   "message": "Operación exitosa",
//   "data": { ... },
//   "timestamp": "2026-01-01T00:00:00"
// }

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;                             // Genérico: puede ser cualquier objeto
    private LocalDateTime timestamp;

    // ── Constructores de utilidad ─────────────────────
    public ApiResponse() {}

    // Respuesta exitosa con datos
    public static <T> ApiResponse<T> ok(String message, T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success   = true;
        r.message   = message;
        r.data      = data;
        r.timestamp = LocalDateTime.now();
        return r;
    }

    // Respuesta exitosa sin datos (ej: "Mensaje enviado")
    public static <T> ApiResponse<T> ok(String message) {
        return ok(message, null);
    }

    // Respuesta de error
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success   = false;
        r.message   = message;
        r.data      = null;
        r.timestamp = LocalDateTime.now();
        return r;
    }

    // ── Getters y Setters ─────────────────────────────
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}