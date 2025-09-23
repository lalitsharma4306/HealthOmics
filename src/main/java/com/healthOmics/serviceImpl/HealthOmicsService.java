package com.healthOmics.serviceImpl;

//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.PutObjectRequest;
import com.healthOmics.dto.request.WorkflowParameterDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
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
////        CreateWorkflowResponse response = new CreateWorkflowResponse();
////        response.arn();
////        response.setId(workflow.id());
////        response.setStatus(workflow.statusAsString());
////        response.setTags(workflow.tags());
////        response.setUuid(workflow.uuid());
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
////        if (parameterTemplate != null) {
////            workflowParameterMap = parameterTemplate.entrySet().stream()
////                    .collect(Collectors.toMap(
////                            Map.Entry::getKey,
////                            e -> WorkflowParameter.builder()
////                                    .description(e.getValue().getDescription())
////                                    .optional(e.getValue().getOptional())
////                                    .build()
////                    ));
////        }
////
////        if (parameterTemplate != null) {
////            requestBuilder.parameterTemplate(workflowParameterMap);
////        }
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

    private File convertToFile(MultipartFile file, String fileName) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
