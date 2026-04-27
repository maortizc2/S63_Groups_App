package com.groupsapp.monolito.config;

import com.groupsapp.monolito.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errores de validación (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    // Errores de negocio (RuntimeException lanzadas desde los servicios)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException ex) {
        String message = ex.getMessage();

        // Determinar el código HTTP según el mensaje
        if (message != null && (
                message.contains("No tienes acceso") ||
                message.contains("No eres miembro") ||
                message.contains("Solo admins") ||
                message.contains("Solo lectura") ||
                message.contains("privado") ||
                message.contains("Forbidden"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(message));
        }

        if (message != null && (
                message.contains("no encontrado") ||
                message.contains("not found"))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(message));
        }

        if (message != null && (
                message.contains("ya está") ||
                message.contains("ya es miembro") ||
                message.contains("ya perteneces"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(message));
        }

        // Error genérico
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message != null ? message : "Error interno"));
    }

    // Cualquier otra excepción no manejada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor"));
    }
}
