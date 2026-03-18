package com.groupsapp.monolito.dto.auth;

public class AuthResponse {

    private String token;           // JWT que el cliente guarda y envía en cada request
    private String type = "Bearer"; // Tipo de token (estándar)
    private Long userId;
    private String username;
    private String email;

    // Constructor rápido para crear la respuesta de login
    public AuthResponse(String token, Long userId, String username, String email) {
        this.token    = token;
        this.type     = "Bearer";
        this.userId   = userId;
        this.username = username;
        this.email    = email;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}