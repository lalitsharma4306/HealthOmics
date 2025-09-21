package com.healthOmics.dto.request;

import lombok.Data;

@Data
public class WorkflowParameterDto {
    private String description;
    private Boolean optional;

    // Getters and setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getOptional() { return optional; }
    public void setOptional(Boolean optional) { this.optional = optional; }
}
