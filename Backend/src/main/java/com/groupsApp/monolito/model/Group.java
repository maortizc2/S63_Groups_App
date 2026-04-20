package com.groupsapp.monolito.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "groups") // "groups" es una palabra reservada en SQL,  pero PostgreSQL lo acepta con comillas
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 255)
    private String avatarUrl;

    // Tipo de suscripción al grupo
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupType type = GroupType.PUBLIC;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ── Relaciones ────────────────────────────────────
    // Quién creó el grupo (dueño/admin principal)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Miembros del grupo
    @JsonIgnore
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<GroupMember> members = new HashSet<>();

    // Canales dentro del grupo
    @JsonIgnore
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Channel> channels = new HashSet<>();

    // ── Hooks ─────────────────────────────────────────
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Enum de tipo de grupo ─────────────────────────
    public enum GroupType {
        PUBLIC,     // Cualquiera puede unirse
        PRIVATE,    // Solo por invitación
        RESTRICTED  // Admin aprueba solicitudes
    }

    // ── Getters y Setters ─────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public GroupType getType() { return type; }
    public void setType(GroupType type) { this.type = type; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public Set<GroupMember> getMembers() { return members; }
    public void setMembers(Set<GroupMember> members) { this.members = members; }

    public Set<Channel> getChannels() { return channels; }
    public void setChannels(Set<Channel> channels) { this.channels = channels; }
}