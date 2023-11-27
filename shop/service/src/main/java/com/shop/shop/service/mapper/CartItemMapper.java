package com.shop.shop.service.mapper;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.shop.dto.CartItemDto;
import com.shop.shop.model.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper extends CommonCrudMapper<CartItem, CartItemDto> {
}
