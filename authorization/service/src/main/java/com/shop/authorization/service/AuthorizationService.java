package com.shop.authorization.service;

import com.shop.authorization.dto.auth.AuthorizationForm;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import jakarta.validation.Valid;

public interface AuthorizationService {

    AccessRefreshTokens authorizeUser(@Valid AuthorizationForm form);

}
