package com.shop.product.service.config;

import com.shop.product.dao.CategoryRepository;
import com.shop.product.dao.DiscountRepository;
import com.shop.product.dao.ProductRepository;
import com.shop.product.service.ProductService;
import com.shop.product.service.impl.ProductServiceImpl;
import com.shop.product.service.mappers.ProductMapper;
import com.shop.product.service.mappers.ProductMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@TestConfiguration
@PropertySource("application-test.properties")
public class ProductServiceContextConfiguration {

    @MockBean
    public ProductRepository productRepository;

    @MockBean
    public CategoryRepository categoryRepository;

    @MockBean
    public DiscountRepository discountRepository;

    @Bean
    public ProductMapper productMapper() {
        return new ProductMapperImpl();
    }

    @Bean
    public ProductService productService() {
        return new ProductServiceImpl(
                productRepository,
                categoryRepository,
                discountRepository,
                productMapper()
        );
    }

}
