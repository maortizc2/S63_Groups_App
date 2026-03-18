package com.groupsapp.monolito.dto.group;

import com.groupsapp.monolito.model.Group;
import java.time.LocalDateTime;

public class GroupDTO {

    private Long id;
    private String name;
    private String description;
    private String avatarUrl;
    private Group.GroupType type;
    private Long ownerId;
    private String ownerUsername;
    private long memberCount;
    private LocalDateTime createdAt;

    // Constructor que convierte una entidad Group → DTO
    // (nunca exponemos la entidad JPA directamente)
    public static GroupDTO fromEntity(Group group, long memberCount) {
        GroupDTO dto = new GroupDTO();
        dto.id            = group.getId();
        dto.name          = group.getName();
        dto.description   = group.getDescription();
        dto.avatarUrl     = group.getAvatarUrl();
        dto.type          = group.getType();
        dto.ownerId       = group.getOwner().getId();
        dto.ownerUsername = group.getOwner().getUsername();
        dto.memberCount   = memberCount;
        dto.createdAt     = group.getCreatedAt();
        return dto;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Group.GroupType getType() { return type; }
    public void setType(Group.GroupType type) { this.type = type; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public long getMemberCount() { return memberCount; }
    public void setMemberCount(long memberCount) { this.memberCount = memberCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}