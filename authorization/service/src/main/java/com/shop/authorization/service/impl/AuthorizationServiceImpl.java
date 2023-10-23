package com.shop.authorization.service.impl;

import com.shop.authorization.dao.UserDataRepository;
import com.shop.authorization.dao.UserRefreshTokenRepository;
import com.shop.authorization.dto.auth.AuthorizationForm;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.authorization.model.UserData;
import com.shop.authorization.model.UserRefreshToken;
import com.shop.authorization.service.AuthorizationService;
import com.shop.authorization.service.encoder.PasswordEncoder;
import com.shop.authorization.service.exception.authorization.PasswordNotMatchAuthorizationException;
import com.shop.authorization.service.exception.authorization.UserNotFoundAuthorizationException;
import com.shop.authorization.service.jwt.provider.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;

    public AuthorizationServiceImpl(UserDataRepository userDataRepository,
                                    UserRefreshTokenRepository userRefreshTokenRepository,
                                    JwtTokenProvider jwtTokenProvider) {
        this.userDataRepository = userDataRepository;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AccessRefreshTokens authorizeUser(@Valid AuthorizationForm form) {
        checkLoginOrEmail(form);

        UserData user = userDataRepository.getByLoginOrEmail(form.getLoginOrEmail(), form.getLoginOrEmail());

        checkPassword(form, user);

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        saveRefreshTokenInDatabase(user, refreshToken);

        return new AccessRefreshTokens(accessToken, refreshToken);
    }

    private void saveRefreshTokenInDatabase(UserData user, String refreshToken) {
        if (user.getRefreshToken() != null) {
            log.info("Updating refresh token for a user '{}'...", user.getLogin());
            user.getRefreshToken().setToken(refreshToken);
            log.info("Refresh token for the user '{}' has been updated.", user.getLogin());
        } else {
            log.info("Saving a refresh token for the user '{}'...", user.getLogin());
            userRefreshTokenRepository.save(new UserRefreshToken(
                    user,
                    refreshToken
            ));
            log.info("Refresh token has been saved for the user '{}'.", user.getLogin());
        }
    }

    private void checkLoginOrEmail(AuthorizationForm form) {
        if (!userDataRepository.existsByLoginOrEmail(form.getLoginOrEmail(), form.getLoginOrEmail())) {
            log.warn("Incorrect login or email! User '{}' not found!", form.getLoginOrEmail());
            throw new UserNotFoundAuthorizationException(
                    String.format("Incorrect login or email! User '%s' not found!", form.getLoginOrEmail())
            );
        }
    }

    private void checkPassword(AuthorizationForm form, UserData user) {
        if (!PasswordEncoder.match(form.getPassword(), user.getPassword())) {
            log.warn("Authorization exception! Incorrect password!");
            throw new PasswordNotMatchAuthorizationException("Authorization exception! Incorrect password!");
        }
    }
}
