package com.healthOmics.dto.request;

import lombok.Data;

import java.util.Map;
@Data
public class CreateWorkflowRequest {

    private String accelerators;
    private String definitionUri;
    private String definitionZip; // Base64 encoded
    private String description;
    private String engine; // WDL | NEXTFLOW | CWL
    private String main;
    private String name;
    private Map<String, WorkflowParameter> parameterTemplate;
    private Integer storageCapacity;
    private String storageType; // STATIC | DYNAMIC
    private Map<String, String> tags;
    private String workflowBucketOwnerId;

    // Getters & Setters
@Data
    public static class WorkflowParameter {
        private String description;
        private Boolean optional;

        // Getters & Setters
    }
}
