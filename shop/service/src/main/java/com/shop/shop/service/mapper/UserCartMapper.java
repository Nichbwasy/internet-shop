package com.shop.shop.service.mapper;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.shop.dto.UserCartDto;
import com.shop.shop.model.UserCart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserCartMapper extends CommonCrudMapper<UserCart, UserCartDto> {



}
