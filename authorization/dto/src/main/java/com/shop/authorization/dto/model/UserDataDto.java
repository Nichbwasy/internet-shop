package com.shop.authorization.dto.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDataDto {

    private Long id;
    private String login;
    private String password;
    private String email;
    private UserRefreshTokenDto refreshToken;
    private List<RoleDto> roles;

}
