package com.shop.product.service;

import com.shop.product.dto.SubCategoryDto;

import java.util.List;

public interface SubCategoryService {

    List<SubCategoryDto> getAllSubCategories();
    SubCategoryDto getSubCategory(Long id);
    SubCategoryDto addSubCategory(SubCategoryDto subCategoryDto);
    Long removeSubCategory(Long id);
    SubCategoryDto updateSubCategory(SubCategoryDto subCategoryDto);

}
