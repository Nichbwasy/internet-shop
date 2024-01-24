package com.shop.media.service.config;

import com.shop.media.dao.FileExtensionRepository;
import com.shop.media.service.FileExtensionService;
import com.shop.media.service.impl.FileExtensionServiceImpl;
import com.shop.media.service.mapper.FileExtensionMapper;
import com.shop.media.service.mapper.FileExtensionMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class FileExtensionServiceTestConfiguration {

    @MockBean
    public FileExtensionRepository fileExtensionRepository;

    @Bean
    public FileExtensionMapper fileExtensionMapper() {
        return new FileExtensionMapperImpl();
    }

    @Bean
    public FileExtensionService fileExtensionService() {
        return new FileExtensionServiceImpl(
                fileExtensionRepository,
                fileExtensionMapper()
        );
    }

}
