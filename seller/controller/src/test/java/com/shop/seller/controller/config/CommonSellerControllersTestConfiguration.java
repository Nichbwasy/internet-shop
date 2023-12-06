package com.shop.seller.controller.config;

import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.client.UserDataApiClient;
import com.shop.product.client.ProductApiClient;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = {"com.shop.seller.model"})
@EnableJpaRepositories(basePackages = {"com.shop.seller.dao"})
@PropertySource("classpath:application-test.properties")
public class CommonSellerControllersTestConfiguration {

    @MockBean
    public TokensApiClient tokensApiClient;
    @MockBean
    public ProductApiClient productApiClient;
    @MockBean
    public UserDataApiClient userDataApiClient;


}
