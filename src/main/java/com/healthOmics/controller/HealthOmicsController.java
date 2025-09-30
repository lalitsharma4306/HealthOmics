package com.healthOmics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthOmics.dto.RunWorkflowRequestDto;
import com.healthOmics.dto.UpdateWorkflowRequestDto;
import com.healthOmics.dto.request.WorkflowParameterDto;
import com.healthOmics.serviceImpl.CreateAnnotationStoreExample;
import com.healthOmics.serviceImpl.HealthOmicsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.omics.model.CreateWorkflowResponse;
import software.amazon.awssdk.services.omics.model.StartRunResponse;
import software.amazon.awssdk.services.omics.model.WorkflowParameter;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthOmicsController {
    private static final Logger log = LoggerFactory.getLogger(HealthOmicsController.class);

    @Autowired
    private CreateAnnotationStoreExample createAnnotationStoreExample;
    @Autowired
    private HealthOmicsService healthOmicsService;

    //    @GetMapping("/createAnnotationStore")
//    public ResponseEntity<?> createAnnotationStore(){
//        return new ResponseEntity<>(createAnnotationStoreExample.createAnnotationStore(), HttpStatus.OK);
//    }
//    @PostMapping("/create")
//    public ResponseEntity<CreateWorkflowResponse> createWorkflow(
//            @RequestParam String name,
//            @RequestParam String description,
//            @RequestParam(required = false) String zipPath,
//            @RequestParam String engine,
//            @RequestParam(required = false) String main,
//            @RequestParam(required = false) String accelerators,
//            @RequestBody(required = false) Map<String, WorkflowParameterDto> parameterTemplate,
//            @RequestParam(required = false) Integer storageCapacity,
//            @RequestParam(required = false) String storageType,
//            @RequestParam(required = false) Map<String, String> tags,
//            @RequestParam(required = false) String workflowBucketOwnerId,
//            @RequestParam(required = false) String definitionUri,
//            @RequestParam(required = false) String readmeMarkdown,
//            @RequestParam(required = false) String readmePath,
//            @RequestParam(required = false) String readmeUri,
//            @RequestParam String requestId
//    ) throws Exception {
//
//        CreateWorkflowResponse response = healthOmicsService.createWorkflow(
//                name,
//                description,
//                zipPath,
//                engine,
//                main,
//                accelerators,
//                parameterTemplate,
//                storageCapacity,
//                storageType,
//                tags,
//                workflowBucketOwnerId,
//                definitionUri,
//                readmeMarkdown,
//                readmePath,
//                readmeUri,
//                requestId
//        );
//
//        return ResponseEntity.status(201).body(response);
//    }
    @PostMapping("/createWorkflowWithZIP")
    public ResponseEntity<?> createWorkflowWithZIP(@RequestParam String name, @RequestParam MultipartFile file) {
        try {
            CreateWorkflowResponse workflowWithZIP = healthOmicsService.createWorkflowWithZIP(name, file);
//            return ResponseEntity.status(201).body(workflowWithZIP);
            log.info("workflowWithZIP response : " + workflowWithZIP.toString());
            return ResponseEntity.ok(workflowWithZIP.toString());
        } catch (Exception e) {
            return ResponseEntity.status(422).body("❌ Failed to create workflow: " + e.getMessage());
        }
    }

    @PutMapping("/workflows")
    public ResponseEntity<String> updateWorkflow(@RequestBody UpdateWorkflowRequestDto dto) {
        log.info("Received update request for workflow: {}", dto);
        healthOmicsService.updateWorkflow(dto);
        return ResponseEntity.ok("Workflow update request submitted successfully");
    }

    //    @PostMapping("/createPrivateWorkFlow")
//    public String createWorkflow(
//            @RequestParam String name,
//            @RequestParam String s3Uri) {
//        return healthOmicsService.createPrivateWorkflow(name, s3Uri);
//    }
    @DeleteMapping("/{workflowId}")
    public ResponseEntity<String> deleteWorkflow(@PathVariable String workflowId) {
        log.info("Received delete request for workflowId={}", workflowId);

        healthOmicsService.deleteWorkflow(workflowId);

        return ResponseEntity.accepted().body("Workflow deletion request submitted successfully");
    }
    @PostMapping("/run")
    public ResponseEntity<?> runWorkflow(@RequestParam String workflowId,
                                         @RequestParam String fastqPath,
                                         @RequestParam String roleArn,
                                         @RequestParam String outputPath) {
        String runId = healthOmicsService.runWorkflow(workflowId, fastqPath, roleArn, outputPath);
        return ResponseEntity.ok("Workflow started with RunId: " + runId);
    }

    // Check Status
    @GetMapping("/status/{runId}")
    public ResponseEntity<?> checkStatus(@PathVariable String runId) {
        String status = healthOmicsService.getRunStatus(runId);
        return ResponseEntity.ok("Run " + runId + " status: " + status);
    }
    @PostMapping("/runWorkflow")
    public ResponseEntity<?> runWorkflow(@RequestBody RunWorkflowRequestDto dto) {
        try {
//            Map<String, Document> params = new HashMap<>();
//            params.put("hello_workflow.input_fastq", Document.fromString(dto.getFastqS3Path()));
//
//            StartRunRequest request = StartRunRequest.builder()
//                    .workflowId(dto.getWorkflowId())
//                    .roleArn(dto.getRoleArn())
//                    .outputUri(dto.getOutputS3Path())
//                    .name(dto.getRunName())
//                    .requestId(dto.getRunName() + "-req")
//                    .parameters(params)
//                    .logLevel("ALL")
//                    .retentionMode("REMOVE")
//                    .storageType("DYNAMIC")
//                    .build();
//
//            StartRunResponse response = omicsClient.startRun(request);
//
//            Map<String, Object> result = new HashMap<>();
//            result.put("runId", response.id());
//            result.put("uuid", response.uuid());
//            result.put("status", response.status());
//            result.put("outputUri", response.runOutputUri());
//
//            return ResponseEntity.ok(result);
            StartRunResponse startRunResponse = healthOmicsService.runWorkflowWithBody(dto.getWorkflowId(), dto.getRoleArn(), dto.getFastqS3Path(), dto.getOutputS3Path(), dto.getRunName());
            return ResponseEntity.ok("Workflow started with RunId: " + startRunResponse.id());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
