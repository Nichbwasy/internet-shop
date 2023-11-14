package com.shop.product.service;

import com.shop.product.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAllCategories();
    CategoryDto getCategory(Long id);
    CategoryDto addCategory(CategoryDto categoryDto);
    Long removeCategory(Long id);
    CategoryDto updateCategory(CategoryDto categoryDto);


}
