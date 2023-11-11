package com.shop.authorization.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessRefreshTokens {

    private String accessToken;
    private String refreshToken;

}
