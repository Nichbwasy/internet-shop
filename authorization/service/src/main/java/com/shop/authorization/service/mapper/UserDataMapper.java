package com.shop.authorization.service.mapper;

import com.shop.authorization.dto.model.UserDataDto;
import com.shop.authorization.model.UserData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDataMapper {

    UserData mapToModel(UserDataDto userDataDto);
    UserDataDto mapToDto(UserData userData);

}
