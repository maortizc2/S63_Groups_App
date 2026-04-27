package com.groupsapp.monolito.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "group_members",
        uniqueConstraints = {
           // Un usuario solo puede estar una vez en cada grupo
            @UniqueConstraint(columnNames = {"user_id", "group_id"})
        })
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Relaciones ────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // ── Rol dentro del grupo ──────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role = MemberRole.MEMBER;

    @Column(updatable = false)
    private LocalDateTime joinedAt;

    // ── Hook ──────────────────────────────────────────
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

    // ── Enum de rol ───────────────────────────────────
    public enum MemberRole {
        OWNER,      // Creador del grupo
        ADMIN,      // Administrador
        MEMBER      // Miembro regular
    }

// ── Getters y Setters ─────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    public MemberRole getRole() { return role; }
    public void setRole(MemberRole role) { this.role = role; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
}