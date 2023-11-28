package com.shop.seller.service.mapper;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.seller.dto.SellerProductDto;
import com.shop.seller.model.SellerProduct;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SellerProductMapper extends CommonCrudMapper<SellerProduct, SellerProductDto> {



}
