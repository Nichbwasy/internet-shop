package com.shop.authorization.service.mapper;

import com.shop.authorization.dto.model.AuthorityDto;
import com.shop.authorization.model.Authority;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {

    Authority mapToModel(AuthorityDto authorityDto);
    AuthorityDto mapToDto(Authority authority);

}
