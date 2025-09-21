package com.healthOmics.serviceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.omics.OmicsClient;
import software.amazon.awssdk.services.omics.model.CreateAnnotationStoreRequest;
import software.amazon.awssdk.services.omics.model.ReferenceItem;
import software.amazon.awssdk.services.omics.model.SseConfig;
import software.amazon.awssdk.services.omics.model.StoreOptions;
import software.amazon.awssdk.services.omics.model.TsvStoreOptions;
import software.amazon.awssdk.services.omics.model.CreateAnnotationStoreResponse;
import software.amazon.awssdk.services.omics.model.OmicsException;

import java.util.HashMap;
import java.util.Map;
@Service
public class CreateAnnotationStoreExample {
    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretKey}")
    private String secretKey;
    @Value("${aws.region}")
    public String AWS_REGION;

    public String createAnnotationStore(){
        // Set region
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                accessKeyId,
                secretKey
        );
        Region region = Region.US_EAST_1;  // change as needed
        OmicsClient omics = OmicsClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, accessKeyId)
                ))
                .build();

        try {
            // Build ReferenceItem if you have a reference ARN
            ReferenceItem referenceItem = ReferenceItem.builder()
                    .referenceArn("arn:aws:s3:::senthil-texium2023")
                    .build();

            // Build SSE config if you want encryption
            SseConfig sseConfig = SseConfig.builder()
                    .type("KMS")
                    .keyArn("arn:aws:s3:::senthil-texium2023")
                    .build();

            // Build store options (this example for TSV with custom options)
            // If using TSV, we use TsvStoreOptions
            TsvStoreOptions tsvOpts = TsvStoreOptions.builder()
                    // Example option fields — fill in as per what TSV format requires
                    .build();
            StoreOptions storeOptions = StoreOptions.builder()
                    .tsvStoreOptions(tsvOpts)
                    .build();

            // Tags (optional)
            Map<String, String> tags = new HashMap<>();
            tags.put("project", "gene-annotation");
            tags.put("env", "dev");

            // Create request
            CreateAnnotationStoreRequest request = CreateAnnotationStoreRequest.builder()
                    .name("test_store")             // must follow pattern: starts with lowercase a-z, etc.
                    .description("An annotation store for our variant data")
                    .reference(referenceItem)
                    .storeFormat("TSV")                     // or "GFF" / "VCF"
                    .storeOptions(storeOptions)
                    .sseConfig(sseConfig)
                    .tags(tags)
                    .versionName("v1")                      // optional
                    .build();

            // Call API
            CreateAnnotationStoreResponse response = omics.createAnnotationStore(request);

            // Read result
            System.out.println("Store Created:");
            System.out.println("ID: " + response.id());
            System.out.println("Name: " + response.name());
            System.out.println("Status: " + response.status());
            System.out.println("Store Format: " + response.storeFormat());
            System.out.println("VersionName: " + response.versionName());
            System.out.println("Creation Time: " + response.creationTime());
            return response.toString();

        } catch (OmicsException e) {
            System.err.println("Error creating annotation store: " + e.awsErrorDetails().errorMessage());
            // Handle error: maybe ValidationException, AccessDeniedException, etc.
            return "Error creating annotation store: " + e.awsErrorDetails().errorMessage();
        } finally {
            omics.close();
        }
    }
}
