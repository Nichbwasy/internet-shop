package com.shop.product.service;

import com.shop.product.dto.CategoryDto;
import com.shop.product.dto.form.AddOrRemoveForm;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAllCategories();
    CategoryDto getCategory(Long id);
    CategoryDto addCategory(CategoryDto categoryDto);
    Long removeCategory(Long id);
    CategoryDto updateCategory(CategoryDto categoryDto);
    CategoryDto addSubCategory(AddOrRemoveForm form);
    CategoryDto removeSubCategory(AddOrRemoveForm form);
    List<CategoryDto> findCategoriesByIds(List<Long> ids);


}
