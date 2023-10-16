package com.shop.authorization.dto.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRefreshTokenDto {

    private Long id;
    private UserDataDto user;
    private String token;

}
