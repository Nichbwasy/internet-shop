package com.shop.product.service.config;

import com.shop.product.dao.CategoryRepository;
import com.shop.product.dao.DiscountRepository;
import com.shop.product.dao.ProductRepository;
import com.shop.product.service.ProductService;
import com.shop.product.service.impl.ProductServiceImpl;
import com.shop.product.service.mappers.ProductMapper;
import com.shop.product.service.mappers.ProductMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = {"com.shop.product.model"})
@EnableJpaRepositories(basePackages = {"com.shop.product.dao"})
@PropertySource("application-test.properties")
@EnableTransactionManagement
public class ProductServiceDatabaseTestsConfiguration {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private CategoryRepository categoryRepository;

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
