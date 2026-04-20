package com.groupsapp.monolito.dto.group;

import com.groupsapp.monolito.model.User;

public class UserSearchDTO {

    private Long   userId;
    private String username;
    private String email;
    private String avatarUrl;
    private String status;

    public static UserSearchDTO fromEntity(User u) {
        UserSearchDTO dto = new UserSearchDTO();
        dto.userId   = u.getId();
        dto.username = u.getUsername();
        dto.email    = u.getEmail();
        dto.avatarUrl= u.getAvatarUrl();
        dto.status   = u.getStatus().name();
        return dto;
    }

    public Long   getUserId()           { return userId; }
    public void   setUserId(Long v)     { this.userId = v; }
    public String getUsername()         { return username; }
    public void   setUsername(String v) { this.username = v; }
    public String getEmail()            { return email; }
    public void   setEmail(String v)    { this.email = v; }
    public String getAvatarUrl()        { return avatarUrl; }
    public void   setAvatarUrl(String v){ this.avatarUrl = v; }
    public String getStatus()           { return status; }
    public void   setStatus(String v)   { this.status = v; }
}
