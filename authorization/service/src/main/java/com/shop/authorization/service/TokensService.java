package com.shop.authorization.service;

import com.shop.authorization.dto.token.AccessRefreshTokens;

public interface TokensService {

    AccessRefreshTokens refreshTokens(Long userId, String refreshToken);

}
