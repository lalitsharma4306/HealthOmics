package com.healthOmics.controller;

import com.healthOmics.dto.request.WorkflowParameterDto;
import com.healthOmics.serviceImpl.CreateAnnotationStoreExample;
import com.healthOmics.serviceImpl.HealthOmicsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.omics.model.CreateWorkflowResponse;
import software.amazon.awssdk.services.omics.model.WorkflowParameter;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthOmicsController {

    @Autowired
    private CreateAnnotationStoreExample createAnnotationStoreExample;
    @Autowired
    private HealthOmicsService healthOmicsService;
    @GetMapping("/createAnnotationStore")
    public ResponseEntity<?> createAnnotationStore(){
        return new ResponseEntity<>(createAnnotationStoreExample.createAnnotationStore(), HttpStatus.OK);
    }
    @PostMapping("/create")
    public ResponseEntity<CreateWorkflowResponse> createWorkflow(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) String zipPath,
            @RequestParam String engine,
            @RequestParam(required = false) String main,
            @RequestParam(required = false) String accelerators,
            @RequestBody(required = false) Map<String, WorkflowParameterDto> parameterTemplate,
            @RequestParam(required = false) Integer storageCapacity,
            @RequestParam(required = false) String storageType,
            @RequestParam(required = false) Map<String, String> tags,
            @RequestParam(required = false) String workflowBucketOwnerId,
            @RequestParam(required = false) String definitionUri,
            @RequestParam(required = false) String readmeMarkdown,
            @RequestParam(required = false) String readmePath,
            @RequestParam(required = false) String readmeUri,
            @RequestParam String requestId
    ) throws Exception {

        CreateWorkflowResponse response = healthOmicsService.createWorkflow(
                name,
                description,
                zipPath,
                engine,
                main,
                accelerators,
                parameterTemplate,
                storageCapacity,
                storageType,
                tags,
                workflowBucketOwnerId,
                definitionUri,
                readmeMarkdown,
                readmePath,
                readmeUri,
                requestId
        );

        return ResponseEntity.status(201).body(response);
    }
    @PostMapping("/createPrivateWorkFlow")
    public String createWorkflow(
            @RequestParam String name,
            @RequestParam String s3Uri) {
        return healthOmicsService.createPrivateWorkflow(name, s3Uri);
    }
}
