package com.groupsapp.monolito.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_status",
        uniqueConstraints = {
           // Cada usuario tiene un solo estado por mensaje
            @UniqueConstraint(columnNames = {"message_id", "user_id"})
        })
public class MessageStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Relaciones ────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                              // El destinatario

    // ── Los "chulitos" ────────────────────────────────
    private LocalDateTime deliveredAt;              // ✓✓ gris  - entregado
    private LocalDateTime readAt;                   // ✓✓ azul  - leído

    // ── Métodos de utilidad ───────────────────────────
    public boolean isDelivered() {
        return deliveredAt != null;
    }

    public boolean isRead() {
        return readAt != null;
    }

    public void markAsDelivered() {
        if (this.deliveredAt == null) {
            this.deliveredAt = LocalDateTime.now();
        }
    }

    public void markAsRead() {
        markAsDelivered();                          // Si leyó, también fue entregado
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }

    // ── Getters y Setters ─────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}