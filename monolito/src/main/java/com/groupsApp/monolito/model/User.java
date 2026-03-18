package com.groupsapp.monolito.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;                        // Guardado con BCrypt

    @Column(length = 255)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.OFFLINE; // Online/Offline

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastSeen;

    // ── Relaciones ────────────────────────────────────
    // Un usuario puede pertenecer a muchos grupos
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<GroupMember> groupMemberships = new HashSet<>();

    // ── Hooks de ciclo de vida ────────────────────────
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastSeen  = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastSeen = LocalDateTime.now();
    }

    // ── Enum de estado ────────────────────────────────
    public enum UserStatus {
        ONLINE,
        OFFLINE
    }

     // ── Getters y Setters ─────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastSeen() { return lastSeen; }

    public Set<GroupMember> getGroupMemberships() { return groupMemberships; }
    public void setGroupMemberships(Set<GroupMember> groupMemberships) {
        this.groupMemberships = groupMemberships;
    }
}