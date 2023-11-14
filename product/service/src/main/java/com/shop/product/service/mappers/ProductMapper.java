package com.shop.product.service.mappers;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.product.dto.ProductDto;
import com.shop.product.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper extends CommonCrudMapper<Product, ProductDto> {



}
