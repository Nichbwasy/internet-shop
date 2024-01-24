package com.shop.media.controller.config;

import com.shop.authorization.client.TokensApiClient;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-test.properties")
public class CommonMediaMicroserviceTestConfiguration {

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

    @MockBean
    public TokensApiClient tokensApiClient;


}
