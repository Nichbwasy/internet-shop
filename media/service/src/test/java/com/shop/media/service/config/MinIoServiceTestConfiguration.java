package com.shop.media.service.config;

import com.shop.media.dao.MinIoStorage;
import com.shop.media.service.MinIoService;
import com.shop.media.service.impl.MinIoServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MinIoServiceTestConfiguration {

    @MockBean
    public MinIoStorage minIoStorage;

    @Bean
    public MinIoService minIoService() {
        return new MinIoServiceImpl(
          minIoStorage
        );
    }

}
