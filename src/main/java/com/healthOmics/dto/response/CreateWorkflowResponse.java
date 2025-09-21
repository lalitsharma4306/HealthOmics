package com.healthOmics.dto.response;

import lombok.Data;

import java.util.Map;
@Data
public class CreateWorkflowResponse {

    private String arn;
    private String id;
    private String status; // CREATING | ACTIVE | FAILED ...
    private Map<String, String> tags;
    private String uuid;

    // Getters & Setters
}
