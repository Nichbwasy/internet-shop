package com.shop.authorization.dto.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    private Long id;
    private String name;
    private List<AuthorityDto> authorities;

}
