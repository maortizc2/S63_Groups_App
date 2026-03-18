package com.groupsapp.monolito.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "channels")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 300)
    private String description;

    // Si es canal de solo lectura (solo admins pueden escribir)
    @Column(nullable = false)
    private Boolean readOnly = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ── Relaciones ────────────────────────────────────
    // A qué grupo pertenece este canal
    @JsonIgnore // Esto le dice a Jackson "no intentes serializar esta relación a JSON" — el cliente no necesita ver el grupo completo dentro de cada canal porque ya sabe a qué grupo pertenece.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // Mensajes enviados en este canal
    @JsonIgnore
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")                       // Historial ordenado por tiempo
    private List<Message> messages = new ArrayList<>();

    // ── Hook ──────────────────────────────────────────
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Getters y Setters ─────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getReadOnly() { return readOnly; }
    public void setReadOnly(Boolean readOnly) { this.readOnly = readOnly; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}