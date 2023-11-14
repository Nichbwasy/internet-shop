package com.shop.product.service.mappers;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.product.dto.CategoryDto;
import com.shop.product.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends CommonCrudMapper<Category, CategoryDto> {

}
