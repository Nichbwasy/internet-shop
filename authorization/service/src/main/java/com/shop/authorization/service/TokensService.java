package com.shop.authorization.service;

import com.shop.authorization.dto.token.AccessRefreshTokens;

public interface TokensService {

    AccessRefreshTokens refreshTokens(String refreshToken);

}
