package com.adregamdi.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSS3Config {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public Ama
}
