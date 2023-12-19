package com.shop.seller.service.mapper;

import com.shop.authorization.dto.api.user.SellerUserDataDto;
import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.seller.dto.control.SellerDetailsDto;
import com.shop.seller.model.SellerInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SellerDetailsMapper extends CommonCrudMapper<SellerInfo, SellerDetailsDto> {
    @Mapping(ignore = true, target = "id")
    void mapSellerUserData(SellerUserDataDto dto, @MappingTarget SellerDetailsDto target);

}
