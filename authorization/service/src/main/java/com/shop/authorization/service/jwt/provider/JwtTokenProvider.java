package com.shop.authorization.service.jwt.provider;

import com.shop.authorization.model.UserData;
import com.shop.authorization.service.exception.jwt.provider.AccessTokenGenerationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Integer ACCESS_TOKEN_LIFETIME;
    private final Integer REFRESH_TOKEN_LIFETIME;
    private final SecretKey ACCESS_TOKEN_SECRET;
    private final SecretKey REFRESH_TOKEN_SECRET;

    public JwtTokenProvider(
            @Value("${security.jwt.access-token.lifetime}") Integer accessTokenLifetime,
            @Value("${security.jwt.refresh-token.lifetime}") Integer refreshTokenLifetime,
            @Value("${security.jwt.access-token.secret}") String accessSecret,
            @Value("${security.jwt.refresh-token.secret}") String refreshSecret) {
        this.ACCESS_TOKEN_LIFETIME = accessTokenLifetime;
        this.REFRESH_TOKEN_LIFETIME = refreshTokenLifetime;
        this.ACCESS_TOKEN_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.REFRESH_TOKEN_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
    }

    public String generateAccessToken(@NotNull UserData user) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Instant expirationTime = now.plusSeconds(ACCESS_TOKEN_LIFETIME)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();

            Set<String> authorities = new HashSet<>();
            user.getRoles().forEach(role -> {
                authorities.add(role.getName());
                role.getAuthorities().forEach(authority -> {
                    authorities.add(authority.getName());
                });
            });

            return Jwts.builder()
                    .id(user.getId().toString())
                    .subject(user.getLogin())
                    .expiration(Date.from(expirationTime))
                    .signWith(ACCESS_TOKEN_SECRET, Jwts.SIG.HS512)
                    .claim("authorities", authorities)
                    .compact();
        } catch (Exception e) {
            log.error("Exception while generating access token for user '{}'! {}", user.getLogin(), e.getMessage());
            throw new AccessTokenGenerationException(
                    String.format("Exception while generating access token for user '%s'! %s",
                            user.getLogin(), e.getMessage())
            );
        }
    }

    public String generateRefreshToken(@NotNull UserData user) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Instant expirationTime = now.plusSeconds(REFRESH_TOKEN_LIFETIME)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();

            return Jwts.builder()
                    .id(user.getId().toString())
                    .expiration(Date.from(expirationTime))
                    .signWith(REFRESH_TOKEN_SECRET, Jwts.SIG.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Exception while generating refresh token for user '{}'! {}", user.getLogin(), e.getMessage());
            throw new AccessTokenGenerationException(
                    String.format("Exception while generating refresh token for user '%s'! %s",
                            user.getLogin(), e.getMessage())
            );
        }

    }

}
