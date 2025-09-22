package com.healthOmics.serviceImpl;

import com.healthOmics.dto.request.WorkflowParameterDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.omics.OmicsClient;
import software.amazon.awssdk.services.omics.model.CreateWorkflowRequest.Builder;
import software.amazon.awssdk.services.omics.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HealthOmicsService {

    private final OmicsClient omicsClient;

    public HealthOmicsService(OmicsClient omicsClient) {
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
//    public String createPrivateWorkflow(String workflowName, String s3Uri) {
//        // Define parameters template
//        Map<String, WorkflowParameter> parameterTemplate = Map.of(
//                "input_file", WorkflowParameter.builder()
//                        .description("S3 path to the input FASTQ file")
//                        .optional(false)
//                        .build(),
//                "output_dir", WorkflowParameter.builder()
//                        .description("S3 output directory for results")
//                        .optional(false)
//                        .build()
//        );
//
//        // Build request
//        CreateWorkflowRequest request = CreateWorkflowRequest.builder()
//                .name(workflowName)
//                .definitionUri(s3Uri)
//                .parameterTemplate(parameterTemplate)
//                .build();
//
//        // Call API
//        CreateWorkflowResponse response = omicsClient.createWorkflow(request);
//
//        return response.arn(); // Return Workflow ARN
//    }


    public String createPrivateWorkflow(String workflowName, String s3Uri) {
        try {
            Map<String, WorkflowParameter> parameterTemplate = Map.of(
                    "input_file", WorkflowParameter.builder()
                            .description("Input FASTQ file")
                            .optional(false)   // mark as required/optional
                            .build(),
                    "output_dir", WorkflowParameter.builder()
                            .description("Output directory")
                            .optional(true)
                            .build()
            );
            CreateWorkflowRequest request = CreateWorkflowRequest.builder()
                    .name("my-private-workflow")
                    .description("Private workflow for genomic analysis")
                    .engine("WDL")  // or CWL / NEXTFLOW
                    .definitionUri(s3Uri)
                    .parameterTemplate(parameterTemplate)
                    .build();

            CreateWorkflowResponse response = omicsClient.createWorkflow(request);

            System.out.println("Workflow ARN: " + response.arn());
            return response.arn();

        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
            return e.getMessage();
        }
    }

}
