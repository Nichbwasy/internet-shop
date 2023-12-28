package com.shop.media.service.mapper;

import com.shop.common.utils.all.mapping.CommonCyclingAvoidingContext;
import com.shop.media.dto.ProductMediaDto;
import com.shop.media.model.ProductMedia;
import org.mapstruct.Context;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMediaMapper {

    ProductMedia mapToModel(ProductMediaDto dto, @Context CommonCyclingAvoidingContext context);
    @InheritInverseConfiguration
    ProductMediaDto mapToDto(ProductMedia model, @Context CommonCyclingAvoidingContext context );



}
