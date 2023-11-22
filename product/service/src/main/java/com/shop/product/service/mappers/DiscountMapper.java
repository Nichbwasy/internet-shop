package com.shop.product.service.mappers;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.product.dto.DiscountDto;
import com.shop.product.model.Discount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountMapper extends CommonCrudMapper<Discount, DiscountDto> {

}
