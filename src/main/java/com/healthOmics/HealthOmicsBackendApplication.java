package com.healthOmics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.omics.OmicsClient;

@SpringBootApplication
public class HealthOmicsBackendApplication {
	@Value("${aws.accessKeyId}")
	private String accessKeyId;

	@Value("${aws.secretKey}")
	private String secretKey;

	public static void main(String[] args) {
		SpringApplication.run(HealthOmicsBackendApplication.class, args);
	}

	@Bean
	public OmicsClient omicsClient() {
		return OmicsClient.builder()
				.region(Region.US_EAST_1)
				.credentialsProvider(
						StaticCredentialsProvider.create(
								AwsBasicCredentials.create(accessKeyId, secretKey)
						)
				)// Replace with your AWS region
				.build();
	}
}
