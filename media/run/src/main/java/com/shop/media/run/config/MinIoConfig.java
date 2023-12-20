package com.shop.media.run.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIoConfig {

    @Value("${minio.url}")
    private String minioUrl;
    @Value("${minio.credentials.login}")
    private String minioLogin;
    @Value("${minio.credentials.password}")
    private String minioPassword;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioLogin, minioPassword)
                .build();
    }

}
