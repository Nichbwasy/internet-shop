package com.shop.authorization.service.mapper;

import com.shop.authorization.dto.model.UserRefreshTokenDto;
import com.shop.authorization.model.UserRefreshToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRefreshTokenMapper {

    UserRefreshToken mapToModel(UserRefreshTokenDto userRefreshTokenDto);
    UserRefreshTokenDto mapToDto(UserRefreshToken userRefreshToken);

}
