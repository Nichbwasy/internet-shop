package com.shop.product.service.config;

import com.shop.product.dao.SubCategoryRepository;
import com.shop.product.service.SubCategoryService;
import com.shop.product.service.impl.SubCategoryServiceImpl;
import com.shop.product.service.mappers.SubCategoryMapper;
import com.shop.product.service.mappers.SubCategoryMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SubCategoryServiceContextConfiguration {

    @MockBean
    private SubCategoryRepository subCategoryRepository;

    @Bean
    public SubCategoryMapper subCategoryMapper() {
        return new SubCategoryMapperImpl();
    }

    @Bean
    public SubCategoryService subCategoryService() {
        return new SubCategoryServiceImpl(subCategoryRepository, subCategoryMapper());
    }

}
