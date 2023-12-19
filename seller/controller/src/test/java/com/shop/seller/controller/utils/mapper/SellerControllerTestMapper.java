package com.shop.seller.controller.utils.mapper;

import com.shop.product.dto.ProductDto;
import com.shop.product.dto.form.product.NewProductForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SellerControllerTestMapper {

    SellerControllerTestMapper INSTANCE = Mappers.getMapper(SellerControllerTestMapper.class);

    ProductDto mapNewProductFormToDto(NewProductForm form);


}
