package com.shop.authorization.service;

import com.shop.authorization.dto.registration.RegistrationForm;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import jakarta.validation.Valid;

public interface RegistrationService {

    AccessRefreshTokens registerUser(@Valid RegistrationForm form);

}
