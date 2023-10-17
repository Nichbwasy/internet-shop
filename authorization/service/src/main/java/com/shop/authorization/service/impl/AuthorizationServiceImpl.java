package com.shop.authorization.service.impl;

import com.shop.authorization.dao.UserDataRepository;
import com.shop.authorization.dao.UserRefreshTokenRepository;
import com.shop.authorization.dto.auth.AuthorizationForm;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.authorization.service.AuthorizationService;
import com.shop.authorization.service.exception.authorization.UserNotFoundAuthorizationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserDataRepository userDataRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public AuthorizationServiceImpl(UserDataRepository userDataRepository,
                                    UserRefreshTokenRepository userRefreshTokenRepository) {
        this.userDataRepository = userDataRepository;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
    }

    @Override
    public AccessRefreshTokens authorizeUser(@Valid AuthorizationForm form) {

        if (!userDataRepository.existsByLoginOrEmail(form.getLoginOrEmail(), form.getLoginOrEmail())) {
            log.warn("Incorrect login or email! User '{}' not found!", form.getLoginOrEmail());
            throw new UserNotFoundAuthorizationException(
                    String.format("Incorrect login or email! User '%s' not found!", form.getLoginOrEmail())
            );
        }

        // TODO: Needs to add JWT tokens to return (add security support and tokens generation)

        return null;

    }
}
