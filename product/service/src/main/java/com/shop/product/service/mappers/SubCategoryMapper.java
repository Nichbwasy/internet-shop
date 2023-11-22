package com.shop.product.service.mappers;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.product.dto.SubCategoryDto;
import com.shop.product.model.SubCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubCategoryMapper extends CommonCrudMapper<SubCategory, SubCategoryDto> {

}
