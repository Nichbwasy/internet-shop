package com.shop.authorization.service.jwt.utils;

import com.shop.authorization.service.exception.jwt.util.JwtParceClaimsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtTokenUtils {

    private final SecretKey ACCESS_TOKEN_SECRET;
    private final SecretKey REFRESH_TOKEN_SECRET;

    public JwtTokenUtils(
            @Value("security.jwt.access-token.secret") String accessTokenSecret,
            @Value("security.jwt.refresh-token.secret") String refreshTokenSectet) {
        this.ACCESS_TOKEN_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessTokenSecret));
        this.REFRESH_TOKEN_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshTokenSectet));
    }

    public Claims getAccessTokenClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(ACCESS_TOKEN_SECRET)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (Exception e) {
            log.warn("Unable get claims from access token! {}", e.getMessage());
            throw new JwtParceClaimsException(
                    String.format("Unable get claims from access token! %s", e.getMessage())
            );
        }
    }

    public Long getUserIdFromRefreshToken(String refreshToken) {
        try {
            return Long.parseLong(
                    Jwts.parser()
                    .verifyWith(REFRESH_TOKEN_SECRET)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload()
                    .getId());
        } catch (Exception e) {
            log.warn("Unable get user id from refresh token! {}", e.getMessage());
            throw new JwtParceClaimsException(
                    String.format("Unable get user id from refresh token! %s", e.getMessage())
            );
        }
    }
}
