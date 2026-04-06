package com.groupsapp.monolito.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Contenido de texto (null si es solo archivo)
    @Column(columnDefinition = "TEXT")
    private String content;

    // Tipo de mensaje
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type = MessageType.TEXT;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ── Relaciones ────────────────────────────────────

    // Quién envió el mensaje
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // OPCIÓN A: Mensaje en un canal de grupo
    // channel_id tiene valor  → mensaje grupal
    // channel_id es null      → mensaje privado (1 a 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = true)
    private Channel channel;

    // OPCIÓN B: Mensaje privado (1 a 1)
    // receiver_id tiene valor → mensaje privado
    // receiver_id es null     → mensaje de canal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = true)
    private User receiver;

    // Archivo adjunto (opcional, null si es solo texto)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = true)
    private FileMetadata file;

    // Estado de entrega/lectura (los "chulitos")
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MessageStatus> statuses = new ArrayList<>();

    // ── Hook único: combina onCreate + validación ─────
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        validateDestination();
    }

    @PreUpdate
    protected void onUpdate() {
        validateDestination();
    }

    // Regla: canal (grupo) O receptor (privado), nunca ambos ni ninguno
    private void validateDestination() {
        if (channel != null && receiver != null) {
            throw new IllegalStateException(
                "Un mensaje no puede tener canal Y receptor privado al mismo tiempo"
            );
        }
        if (channel == null && receiver == null) {
            throw new IllegalStateException(
                "Un mensaje debe tener canal (grupo) O receptor privado"
            );
        }
    }

    // ── Métodos de utilidad ───────────────────────────
    public boolean isGroupMessage() { return channel != null; }
    public boolean isDirectMessage() { return receiver != null; }

    // ── Enum de tipo ──────────────────────────────────
    public enum MessageType {
        TEXT,       // Mensaje de texto
        IMAGE,      // Imagen
        FILE,       // Archivo genérico
        VOICE       // Mensaje de voz (opcional)
    }

     // ── Getters y Setters ─────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public Channel getChannel() { return channel; }
    public void setChannel(Channel channel) { this.channel = channel; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public FileMetadata getFile() { return file; }
    public void setFile(FileMetadata file) { this.file = file; }

    public List<MessageStatus> getStatuses() { return statuses; }
    public void setStatuses(List<MessageStatus> statuses) { this.statuses = statuses; }
}