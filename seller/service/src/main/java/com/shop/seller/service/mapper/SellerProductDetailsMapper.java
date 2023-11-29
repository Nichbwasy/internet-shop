package com.shop.seller.service.mapper;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.product.dto.ProductDto;
import com.shop.seller.dto.control.SellerProductDetailsDto;
import com.shop.seller.model.SellerProduct;
import org.mapstruct.MappingTarget;

public interface SellerProductDetailsMapper extends CommonCrudMapper<SellerProduct, SellerProductDetailsDto> {

    void mapProductDto(ProductDto from, @MappingTarget SellerProductDetailsDto target);

}
