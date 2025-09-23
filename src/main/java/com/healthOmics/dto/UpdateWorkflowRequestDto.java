package com.healthOmics.dto;

import lombok.Data;


public class UpdateWorkflowRequestDto {
    private String workflowId;        // required
    private String name;              // optional
    private String description;       // optional
    private String readmeMarkdown;    // optional
    private Integer storageCapacity;  // optional
    private String storageType;       // optional (STATIC | DYNAMIC)

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReadmeMarkdown() {
        return readmeMarkdown;
    }

    public void setReadmeMarkdown(String readmeMarkdown) {
        this.readmeMarkdown = readmeMarkdown;
    }

    public Integer getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(Integer storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }
}
