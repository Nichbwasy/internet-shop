package com.shop.authorization.service.mapper;

import com.shop.authorization.dto.model.RoleDto;
import com.shop.authorization.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role mapToModel(RoleDto roleDto);
    RoleDto mapToDto(Role role);

}
