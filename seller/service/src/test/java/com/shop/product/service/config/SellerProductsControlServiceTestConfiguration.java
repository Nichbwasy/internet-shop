package com.shop.product.service.config;

import com.shop.authorization.client.TokensApiClient;
import com.shop.media.client.ProductMediaApiClient;
import com.shop.product.client.ProductApiClient;
import com.shop.product.client.ProductCategoryApiClient;
import com.shop.product.client.ProductDiscountApiClient;
import com.shop.seller.dao.SellerInfoRepository;
import com.shop.seller.dao.SellerProductRepository;
import com.shop.seller.service.SellerProductsControlService;
import com.shop.seller.service.impl.SellerProductsControlServiceImpl;
import com.shop.seller.service.mapper.CreateProductFormMapper;
import com.shop.seller.service.mapper.CreateProductFormMapperImpl;
import com.shop.seller.service.mapper.SellerProductDetailsMapper;
import com.shop.seller.service.mapper.SellerProductDetailsMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@TestConfiguration
@PropertySource("application-test.properties")
public class SellerProductsControlServiceTestConfiguration {

    @MockBean
    public SellerInfoRepository sellerInfoRepository;
    @MockBean
    public SellerProductRepository sellerProductRepository;
    @MockBean
    public ProductApiClient productApiClient;
    @MockBean
    public TokensApiClient tokensApiClient;
    @MockBean
    public ProductCategoryApiClient productCategoryApiClient;
    @MockBean
    public ProductDiscountApiClient productDiscountApiClient;
    @MockBean
    public ProductMediaApiClient productMediaApiClient;
    @Bean
    public SellerProductDetailsMapper sellerProductDetailsMapper() {
        return new SellerProductDetailsMapperImpl();
    }
    @Bean
    public CreateProductFormMapper createProductFormMapper() {
        return new CreateProductFormMapperImpl();
    }
    @Bean
    public SellerProductsControlService sellerProductsControlService() {
        return new SellerProductsControlServiceImpl(
                sellerInfoRepository,
                sellerProductRepository,
                productApiClient,
                productCategoryApiClient,
                productDiscountApiClient,
                tokensApiClient,
                sellerProductDetailsMapper(),
                createProductFormMapper(),
                productMediaApiClient
        );
    }

}
