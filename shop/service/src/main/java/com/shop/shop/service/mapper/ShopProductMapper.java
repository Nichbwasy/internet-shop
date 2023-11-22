package com.shop.shop.service.mapper;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.shop.dto.ShopProductDto;
import com.shop.shop.model.ShopProduct;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShopProductMapper extends CommonCrudMapper<ShopProduct, ShopProductDto> {
}
