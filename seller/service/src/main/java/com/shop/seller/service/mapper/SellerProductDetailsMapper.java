package com.shop.seller.service.mapper;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.product.dto.ProductDto;
import com.shop.seller.dto.control.SellerProductDetailsDto;
import com.shop.seller.model.SellerProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SellerProductDetailsMapper extends CommonCrudMapper<SellerProduct, SellerProductDetailsDto> {

    @Mapping(ignore = true, target = "id")
    void mapProductDto(ProductDto from, @MappingTarget SellerProductDetailsDto target);

}
