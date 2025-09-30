package com.healthOmics.dto;

import lombok.Data;

@Data
public class RunWorkflowRequestDto {
    private String workflowId;
    private String roleArn;
    private String fastqS3Path;
    private String outputS3Path;
    private String runName;
}
