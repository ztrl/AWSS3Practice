package com.example.test;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {
    @Bean
    public Bucket bucket() {
        Bucket bucket = new Bucket();
        bucket.setName(System.getenv("S3_BUCKET_NAME"));
        return bucket;
    }
    @Bean
    public AWSCredentials awsCredentials() {
        var environmentVariableCredentialsProvider = new EnvironmentVariableCredentialsProvider();
        AWSCredentials awsCredentials = environmentVariableCredentialsProvider.getCredentials();
        return awsCredentials;
    }
    @Bean
    public AWSCredentialsProvider awsCredentialsProvider(AWSCredentials awsCredentials) {
        AWSStaticCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        return awsCredentialsProvider;
    }
    @Bean
    public AmazonS3 amazonS3Client(AWSCredentialsProvider awsCredentialsProvider) {
        Regions clientRegion = Regions.AP_NORTHEAST_2;
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .withCredentials(awsCredentialsProvider)
                .build();
        return s3Client;
    }
}
