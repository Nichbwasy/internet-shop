package com.shop.product.service.config;

import com.shop.authorization.client.UserDataApiClient;
import com.shop.product.client.ProductApiClient;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
import com.shop.seller.service.AdminSellersControlService;
import com.shop.seller.service.impl.AdminSellersControlServiceImpl;
import com.shop.seller.service.mapper.SellerDetailsMapper;
import com.shop.seller.service.mapper.SellerDetailsMapperImpl;
import com.shop.seller.service.mapper.SellerInfoMapper;
import com.shop.seller.service.mapper.SellerInfoMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@TestConfiguration
@PropertySource("application-test.properties")
public class AdminSellersControlServiceTestConfiguration {

    @MockBean
    public SellerInfoRepository sellerInfoRepository;
    @MockBean
    public SellerProductRepository sellerProductRepository;
    @MockBean
    public UserDataApiClient userDataApiClient;
    @MockBean
    public ProductApiClient productApiClient;
    @Bean
    public SellerInfoMapper sellerInfoMapper() {
        return new SellerInfoMapperImpl();
    }
    @Bean
    public SellerDetailsMapper sellerDetailsMapper() {
        return new SellerDetailsMapperImpl();
    }
    @Bean
    public AdminSellersControlService adminSellersControlService() {
        return new AdminSellersControlServiceImpl(
                sellerInfoRepository,
                sellerProductRepository,
                userDataApiClient,
                productApiClient,
                sellerInfoMapper(),
                sellerDetailsMapper()
        );
    }

}
