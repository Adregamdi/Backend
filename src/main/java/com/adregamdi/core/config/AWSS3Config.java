package com.adregamdi.core.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSS3Config {

    /* 배포 버전에서는 IAM Role을 사용하여 access-key 발급 */
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public BasicAWSCredentials awsCredentialsProvider() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public AmazonS3Client amazonS3Client() {

        ClientConfiguration clientConfiguration = new ClientConfiguration()
                .withConnectionTimeout(300000)
                .withSocketTimeout(300000);

        return (AmazonS3Client) AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentialsProvider()))
                .withClientConfiguration(clientConfiguration)
                .build();
    }
}
