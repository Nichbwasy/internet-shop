package com.shop.media.service.mapper;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.media.dto.ProductMediaDto;
import com.shop.media.model.ProductMedia;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMediaMapper extends CommonCrudMapper<ProductMedia, ProductMediaDto> {
}
