package com.shop.authorization.service.impl;

import com.shop.authorization.dao.UserRefreshTokenRepository;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.authorization.model.UserRefreshToken;
import com.shop.authorization.service.TokensService;
import com.shop.authorization.service.exception.token.TokensNotMatchException;
import com.shop.authorization.service.exception.token.UserNotFoundTokensServiceException;
import com.shop.authorization.service.jwt.provider.JwtTokenProvider;
import com.shop.authorization.service.jwt.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TokensServiceImpl implements TokensService {

    private final UserRefreshTokenRepository userRefreshTokenRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final JwtTokenUtils jwtTokenUtils;

    public TokensServiceImpl(
            UserRefreshTokenRepository userRefreshTokenRepository,
            JwtTokenProvider jwtTokenProvider, JwtTokenUtils jwtTokenUtils) {
        this.userRefreshTokenRepository = userRefreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    public AccessRefreshTokens refreshTokens(String refreshToken) {

        Long userId = jwtTokenUtils.getUserIdFromRefreshToken(refreshToken);

        checkIfUserTokenExists(userId);

        UserRefreshToken userRefreshToken = userRefreshTokenRepository.getByUserId(userId);

        checkIfIsTheLastGeneratedRefreshToken(refreshToken, userRefreshToken);

        String newAccessToken = jwtTokenProvider.generateAccessToken(userRefreshToken.getUser());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userRefreshToken.getUser());
        log.info("New access/refresh tokens has been generated for the user '{}'.", userRefreshToken.getUser().getLogin());

        userRefreshToken.setToken(newRefreshToken);

        return new AccessRefreshTokens(newAccessToken, newRefreshToken);

    }

    private void checkIfIsTheLastGeneratedRefreshToken(String refreshToken, UserRefreshToken userRefreshToken) {
        if (!refreshToken.equals(userRefreshToken.getToken())) {
            log.warn("Unable refresh tokens! Refresh tokens not match for the user with id '{}'!",
                    userRefreshToken.getUser().getLogin());
            throw new TokensNotMatchException(
                    String.format("Unable refresh tokens! Refresh tokens not match for the user with id '%s'!",
                            userRefreshToken.getUser().getLogin())
            );
        }
    }

    private void checkIfUserTokenExists(Long userId) {
        if (!userRefreshTokenRepository.existsByUserId(userId)) {
            log.warn("Unable refresh tokens! User with id '{}' not found!", userId);
            throw new UserNotFoundTokensServiceException(
                    String.format("Unable refresh tokens! User with id '%s' not found!", userId)
            );
        }
    }
}
