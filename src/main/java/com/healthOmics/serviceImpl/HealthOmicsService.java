package com.healthOmics.serviceImpl;

//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.PutObjectRequest;

import com.healthOmics.dto.UpdateWorkflowRequestDto;
import com.healthOmics.dto.request.WorkflowParameterDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.omics.OmicsClient;
import software.amazon.awssdk.services.omics.model.CreateWorkflowRequest.Builder;
import software.amazon.awssdk.services.omics.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HealthOmicsService {
    private static final Logger log = LoggerFactory.getLogger(HealthOmicsService.class);
    private final S3Client s3Client;
    private final String bucketName = "senthil-texium2023";

    private final OmicsClient omicsClient;

    public HealthOmicsService(S3Client s3Client, OmicsClient omicsClient) {
        this.s3Client = s3Client;
        this.omicsClient = omicsClient;
    }

    //    public CreateWorkflowResponse createWorkflow(CreateWorkflowRequest request) {
//
//        CreateWorkflowRequest.Builder awsRequest = CreateWorkflowRequest.builder()
//                .name(request.name())
//                .description(request.description())
//                .accelerators(request.accelerators())
//                .definitionUri(request.definitionUri())
//                .definitionZip(request.definitionZip() != null ? software.amazon.awssdk.core.SdkBytes.fromUtf8String(String.valueOf(request.definitionZip())) : null)
//                .engine(request.engine())
//                .main(request.main())
//                .storageCapacity(request.storageCapacity())
//                .storageType(request.storageType())
//                .workflowBucketOwnerId(request.workflowBucketOwnerId())
//                .tags(request.tags());
//
////        // You can add parameterTemplate if required
////        if (request.parameterTemplate() != null) {
////            request.parameterTemplate().forEach((key, value) -> {
////                awsRequest.parameterTemplate(key, software.amazon.awssdk.services.omics.model.WorkflowParameter.builder()
////                        .description(value.description())
////                        .optional(value.optional())
////                        .build());
////            });
////        }
//
//        CreateWorkflowResponse response = omicsClient.createWorkflow(awsRequest.build());
//

    /// /        CreateWorkflowResponse response = new CreateWorkflowResponse();
    /// /        response.arn();
    /// /        response.setId(workflow.id());
    /// /        response.setStatus(workflow.statusAsString());
    /// /        response.setTags(workflow.tags());
    /// /        response.setUuid(workflow.uuid());
//
//        return response;
//    }
    public CreateWorkflowResponse createWorkflow(String name,
                                                 String description,
                                                 String workflowZipPath,
                                                 String engine,
                                                 String main,
                                                 String accelerators,
                                                 Map<String, WorkflowParameterDto> parameterTemplate,
                                                 Integer storageCapacity,
                                                 String storageType,
                                                 Map<String, String> tags,
                                                 String workflowBucketOwnerId,
                                                 String definitionUri,
                                                 String readmeMarkdown,
                                                 String readmePath,
                                                 String readmeUri,
                                                 String requestId) {


        SdkBytes zipBytes = null;
        try {
            zipBytes = workflowZipPath != null ? SdkBytes.fromByteArray(Files.readAllBytes(Path.of(workflowZipPath))) : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CreateWorkflowRequest.Builder requestBuilder = CreateWorkflowRequest.builder()
                .name(name)
                .description(description)
                .definitionZip(zipBytes)
                .engine(engine)
                .main(main)
                .accelerators(accelerators)
                .storageCapacity(storageCapacity)
                .storageType(storageType)
                .tags(tags)
                .workflowBucketOwnerId(workflowBucketOwnerId)
                .definitionUri(definitionUri)
                .readmeMarkdown(readmeMarkdown)
                .readmePath(readmePath)
                .readmeUri(readmeUri)
                .requestId(requestId);
        Map<String, WorkflowParameter> workflowParameterMap = null;

        if (parameterTemplate != null) {
            workflowParameterMap = parameterTemplate.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> WorkflowParameter.builder()
                                    .description(e.getValue().getDescription())
                                    .optional(e.getValue().getOptional())
                                    .build()
                    ));
        }

        if (parameterTemplate != null) {
            requestBuilder.parameterTemplate(workflowParameterMap);
        }

        CreateWorkflowResponse response = omicsClient.createWorkflow(requestBuilder.build());
//        log.info("response : " + response);
        return response;
    }

    //    public CreateWorkflowResponse createWorkflowWithZIP(String workflowName, MultipartFile file) throws IOException {
//        // Convert MultipartFile → Temp File
//        File tempFile = convertToFile(file, workflowName + ".zip");
//
//        String keyName = "workflows/" + workflowName + ".zip";
//
//        // Step 1: Upload to S3
//        s3Client.putObject(new PutObjectRequest(bucketName, keyName, tempFile));
//        String s3Uri = "s3://" + bucketName + "/" + keyName;
//
//        // Delete temp file
//        tempFile.delete();
//
//        // Step 2: Create workflow in Omics
////        try (OmicsClient omicsClient = OmicsClient.builder()
////                .region(Region.US_EAST_1)
////                .build()) {
////
////            CreateWorkflowRequest request = CreateWorkflowRequest.builder()
////                    .name(workflowName)
////                    .engine("WDL") // or CWL / NEXTFLOW
////                    .definitionUri(s3Uri)
////                    .parameterTemplate(Map.of(
////                            "name", WorkflowParameter.builder()
////                                    .description("Name to greet")
////                                    .optional(false)
////                                    .build()
////                    ))
////                    .build();
////
////            CreateWorkflowResponse response = omicsClient.createWorkflow(request);
////            return response.arn();
////        }
//        CreateWorkflowRequest.Builder requestBuilder = CreateWorkflowRequest.builder()
//                .name(workflowName)
////                .description(description)
////                .definitionZip(zipBytes)
////                .engine(engine)
////                .main(main)
////                .accelerators(accelerators)
////                .storageCapacity(storageCapacity)
////                .storageType(storageType)
////                .tags(tags)
////                .workflowBucketOwnerId(workflowBucketOwnerId)
//                .definitionUri(s3Uri)
////                .readmeMarkdown(readmeMarkdown)
////                .readmePath(readmePath)
////                .readmeUri(readmeUri)
////                .requestId(requestId)
//                ;
//        Map<String, WorkflowParameter> workflowParameterMap = null;
//

    /// /        if (parameterTemplate != null) {
    /// /            workflowParameterMap = parameterTemplate.entrySet().stream()
    /// /                    .collect(Collectors.toMap(
    /// /                            Map.Entry::getKey,
    /// /                            e -> WorkflowParameter.builder()
    /// /                                    .description(e.getValue().getDescription())
    /// /                                    .optional(e.getValue().getOptional())
    /// /                                    .build()
    /// /                    ));
    /// /        }
    /// /
    /// /        if (parameterTemplate != null) {
    /// /            requestBuilder.parameterTemplate(workflowParameterMap);
    /// /        }
//        log.info("requestBuilder : " + requestBuilder);
//        CreateWorkflowResponse response = omicsClient.createWorkflow(requestBuilder.build());
//        log.info("response : " + response);
//        return response;
//    }
    public CreateWorkflowResponse createWorkflowWithZIP(String workflowName, MultipartFile file) throws IOException {
        // Convert MultipartFile → Temp File
        File tempFile = convertToFile(file, workflowName + ".zip");

        String keyName = "workflows/" + workflowName + ".zip";

        // Step 1: Upload to S3 using SDK v2
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3Client.putObject(putObjectRequest, Paths.get(tempFile.getAbsolutePath()));

        String s3Uri = "s3://" + bucketName + "/" + keyName;

        // Delete temp file
        tempFile.delete();

        // Step 2: Create workflow in Omics
        CreateWorkflowRequest request = CreateWorkflowRequest.builder()
                .name(workflowName)
                .definitionUri(s3Uri)
                .build();

        CreateWorkflowResponse response = omicsClient.createWorkflow(request);

        log.info("Workflow created: {}", response);

        return response;
    }

    //    public UpdateWorkflowResponse updateWorkflow(String workflowIdOrArn, String newDescription) {
//        UpdateWorkflowRequest request = UpdateWorkflowRequest.builder()
//                .id(workflowIdOrArn)   // can be workflowId or full ARN
//                .description(newDescription)
//                .build();
//
//        UpdateWorkflowResponse response = omicsClient.updateWorkflow(request);
//
//        log.info("Workflow updated: {}", response);
//
//        return response;
//    }
    public UpdateWorkflowResponse updateWorkflow(UpdateWorkflowRequestDto dto) {

        UpdateWorkflowRequest.Builder builder = UpdateWorkflowRequest.builder()
                .id(dto.getWorkflowId()); // Workflow ID is mandatory

        if (dto.getName() != null && !dto.getName().isBlank()) {
            builder.name(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            builder.description(dto.getDescription());
        }
        if (dto.getReadmeMarkdown() != null && !dto.getReadmeMarkdown().isBlank()) {
            builder.readmeMarkdown(dto.getReadmeMarkdown());
        }
        if (dto.getStorageCapacity() != null) {
            builder.storageCapacity(dto.getStorageCapacity());
        }
        if (dto.getStorageType() != null && !dto.getStorageType().isBlank()) {
            builder.storageType(dto.getStorageType()); // STATIC or DYNAMIC
        }

        UpdateWorkflowResponse response = omicsClient.updateWorkflow(builder.build());

        log.info("Workflow updated: id={}, name={}, description={}",
                dto.getWorkflowId(), dto.getName(), dto.getDescription());

        return response;
    }

    private File convertToFile(MultipartFile file, String fileName) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    public DeleteWorkflowResponse deleteWorkflow(String workflowId) {

        DeleteWorkflowRequest request = DeleteWorkflowRequest.builder()
                .id(workflowId) // Workflow ID (required)
                .build();

        DeleteWorkflowResponse response = omicsClient.deleteWorkflow(request);

        log.info("Workflow deleted: id={}", workflowId);

        return response;
    }

    public String runWorkflow(String workflowId, String fastqS3Path, String roleArn, String outputS3Path) {
        // Input parameters as Map
        Map<String, Document> params = new HashMap<>();
        params.put("hello_fastq.input_fastq", Document.fromString(fastqS3Path));
        Document paramsDocument = Document.fromString("{\"hello_fastq.input_fastq\": \"" + fastqS3Path + "\"}");

        StartRunResponse runResponse = omicsClient.startRun(
                StartRunRequest.builder()
                        .workflowId(workflowId)
                        .roleArn(roleArn)
                        .parameters(paramsDocument) // ✅ Map pass किया
                        .outputUri(outputS3Path)
                        .build()
        );

        return runResponse.id();
    }

    public String getRunStatus(String runId) {
        GetRunResponse runDetails = omicsClient.getRun(
                GetRunRequest.builder()
                        .id(runId)
                        .build()
        );
        return runDetails.statusAsString();
    }

    //    public StartRunResponse startRun(
//            String roleArn,
//            String workflowId,
//            String outputUri,
//            Map<String, Object> parameters,
//            String storageType,
//            Integer storageCapacity,
//            String requestId,
//            String retentionMode,
//            Map<String, String> tags
//    ) {
//        try {
//            StartRunRequest.Builder requestBuilder = StartRunRequest.builder()
//                    .roleArn(roleArn)
//                    .workflowId(workflowId)
//                    .outputUri(outputUri);
//
//            if (parameters != null && !parameters.isEmpty()) {
//                requestBuilder.parameters(parameters);
//            }
//            if (storageType != null) {
//                requestBuilder.storageType(storageType);
//            }
//            if (storageCapacity != null) {
//                requestBuilder.storageCapacity(storageCapacity);
//            }
//            if (requestId != null) {
//                requestBuilder.requestId(requestId);
//            }
//            if (retentionMode != null) {
//                requestBuilder.retentionMode(retentionMode);
//            }
//            if (tags != null && !tags.isEmpty()) {
//                requestBuilder.tags(tags);
//            }
//
//            StartRunResponse response = omicsClient.startRun(requestBuilder.build());
//            System.out.println("Run started successfully: " + response.id());
//            return response;
//
//        } catch (Exception e) {
//            System.err.println("Failed to start run: " + e.getMessage());
//            throw e;
//        }
//    }
    public StartRunResponse runWorkflowWithBody(
            String workflowId,
            String roleArn,
            String fastqS3Path,
            String outputS3Path,
            String runName) {

        // Parameters map (FASTQ file input)
        Map<String, Document> params = new HashMap<>();
        params.put("hello_workflow.input_fastq", Document.fromString(fastqS3Path));
        Document paramsDocument = Document.fromString("{\"hello_fastq.input_fastq\": \"" + fastqS3Path + "\"}");

        // StartRun request
        StartRunRequest request = StartRunRequest.builder()
                .workflowId(workflowId)
                .roleArn(roleArn)
                .outputUri(outputS3Path)
                .name(runName)                // Run का नाम
//                .requestId(runName + "-req")  // unique idempotency token
                .parameters(paramsDocument)           // ✅ अब सही
                .logLevel("ALL")              // Logs CloudWatch में जाएँगे
//                .retentionMode("REMOVE")      // पुराना data remove कर देगा
//                .storageType("DYNAMIC")       // Auto-scale storage
                .build();

        // Call API
        StartRunResponse response = omicsClient.startRun(request);

        // Logs print करें
        System.out.println("✅ Run started successfully!");
        System.out.println("Run ID: " + response.id());
        System.out.println("UUID: " + response.uuid());
        System.out.println("Output URI: " + response.runOutputUri());
        System.out.println("Status: " + response.status());

        return response;
    }

}
