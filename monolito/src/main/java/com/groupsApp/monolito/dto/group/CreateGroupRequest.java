package com.groupsapp.monolito.dto.group;

import com.groupsapp.monolito.model.Group;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class CreateGroupRequest {

@NotBlank(message = "El nombre del grupo es obligatorio")
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private Group.GroupType type = Group.GroupType.PUBLIC;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Group.GroupType getType() { return type; }
    public void setType(Group.GroupType type) { this.type = type; }
}