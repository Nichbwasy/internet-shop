package com.shop.seller.service.mapper;

import com.shop.common.utils.all.mapping.CommonCrudMapper;
import com.shop.seller.dto.SellerInfoDto;
import com.shop.seller.model.SellerInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SellerInfoMapper extends CommonCrudMapper<SellerInfo, SellerInfoDto> {



}
