package com.groupsapp.monolito.dto.group;

import com.groupsapp.monolito.model.GroupMember;

public class MemberDTO {

    private Long userId;
    private String username;
    private String email;
    private String avatarUrl;
    private String role;       // OWNER | ADMIN | MEMBER
    private String status;     // ONLINE | OFFLINE

    public static MemberDTO fromEntity(GroupMember gm) {
        MemberDTO dto = new MemberDTO();
        dto.userId    = gm.getUser().getId();
        dto.username  = gm.getUser().getUsername();
        dto.email     = gm.getUser().getEmail();
        dto.avatarUrl = gm.getUser().getAvatarUrl();
        dto.role      = gm.getRole().name();
        dto.status    = gm.getUser().getStatus().name();
        return dto;
    }

    // Getters y Setters
    public Long getUserId()          { return userId; }
    public void setUserId(Long v)    { this.userId = v; }

    public String getUsername()         { return username; }
    public void setUsername(String v)   { this.username = v; }

    public String getEmail()            { return email; }
    public void setEmail(String v)      { this.email = v; }

    public String getAvatarUrl()        { return avatarUrl; }
    public void setAvatarUrl(String v)  { this.avatarUrl = v; }

    public String getRole()             { return role; }
    public void setRole(String v)       { this.role = v; }

    public String getStatus()           { return status; }
    public void setStatus(String v)     { this.status = v; }
}
