package com.shop.product.service.utils.mapper;

import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.NewProductForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SellerServiceTestMapper {

    SellerServiceTestMapper INSTANCE = Mappers.getMapper(SellerServiceTestMapper.class);

    ProductDto mapNewProductFormToDto(NewProductForm form);

}
