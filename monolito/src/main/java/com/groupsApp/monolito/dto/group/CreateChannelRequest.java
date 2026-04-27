package com.groupsapp.monolito.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateChannelRequest {
@NotBlank(message = "El nombre del canal es obligatorio")
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 300)
    private String description;

    private Boolean readOnly = false;

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getReadOnly() { return readOnly; }
    public void setReadOnly(Boolean readOnly) { this.readOnly = readOnly; }
}