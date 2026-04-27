package com.groupsapp.presence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Entidad que representa el estado de presencia de un usuario.
 *
 * Usamos userId como clave primaria porque sabemos que es único por usuario
 * y el presence-service solo necesita consultar por ese ID.
 * No necesitamos un ID artificial (evita una columna extra).
 */
@Entity
@Table(name = "user_presence")
public class UserPresence {

    @Id
    private Long userId;

    private boolean online;

    private Instant lastSeen;

    // ── Constructores ─────────────────────────────────
    public UserPresence() {
        // Requerido por JPA.
    }

    public UserPresence(Long userId, boolean online, Instant lastSeen) {
        this.userId = userId;
        this.online = online;
        this.lastSeen = lastSeen;
    }

    // ── Getters y Setters ─────────────────────────────
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }

    public Instant getLastSeen() { return lastSeen; }
    public void setLastSeen(Instant lastSeen) { this.lastSeen = lastSeen; }
}