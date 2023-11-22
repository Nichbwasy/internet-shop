package com.shop.product.controller.config;

import com.shop.authorization.client.TokensApiClient;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application-test.properties")
@EntityScan(basePackages = ("com.shop.product.model"))
@EnableJpaRepositories(basePackages = {"com.shop.product.dao"})
public class CommonProductControllerTestConfiguration {

    @MockBean
    public TokensApiClient tokensApiClient;

}
