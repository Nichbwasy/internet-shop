package com.shop.product.service.config;

import com.shop.product.dao.CategoryRepository;
import com.shop.product.dao.SubCategoryRepository;
import com.shop.product.service.CategoryService;
import com.shop.product.service.impl.CategoryServiceImpl;
import com.shop.product.service.mappers.CategoryMapper;
import com.shop.product.service.mappers.CategoryMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CategoryServiceContextConfiguration {

    @MockBean
    private CategoryRepository categoryRepository;
    @MockBean
    private SubCategoryRepository subCategoryRepository;

    @Bean
    public CategoryMapper categoryMapper() {
        return new CategoryMapperImpl();
    }

    @Bean
    public CategoryService categoryService() {
        return new CategoryServiceImpl(
                categoryRepository,
                subCategoryRepository,
                categoryMapper()
        );
    }

}
