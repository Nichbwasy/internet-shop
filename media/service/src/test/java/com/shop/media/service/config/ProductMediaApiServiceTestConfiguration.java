package com.shop.media.service.config;

import com.shop.media.dao.FileExtensionRepository;
import com.shop.media.dao.MediaElementRepository;
import com.shop.media.dao.ProductMediaRepository;
import com.shop.media.service.MinIoService;
import com.shop.media.service.ProductMediaApiService;
import com.shop.media.service.impl.ProductMediaApiServiceImpl;
import com.shop.media.service.mapper.ProductMediaMapper;
import com.shop.media.service.mapper.ProductMediaMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@TestConfiguration
@PropertySource("classpath:application-test.properties")
public class ProductMediaApiServiceTestConfiguration {

    @MockBean
    public ProductMediaRepository productMediaRepository;
    @MockBean
    public MediaElementRepository mediaElementRepository;
    @MockBean
    public FileExtensionRepository fileExtensionRepository;
    @MockBean
    public MinIoService minIoService;
    @Bean
    public ProductMediaMapper productMediaMapper() {
        return new ProductMediaMapperImpl();
    }
    @Bean
    public ProductMediaApiService productMediaApiService() {
        return new ProductMediaApiServiceImpl(
                productMediaRepository,
                mediaElementRepository,
                fileExtensionRepository,
                minIoService,
                productMediaMapper()
        );
    }

}
