package com.shop.authorization.service.jwt.validator;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtTokenValidator {

    private final SecretKey ACCESS_TOKEN_SECRET;
    private final SecretKey REFRESH_TOKEN_SECRET;

    public JwtTokenValidator(
            @Value("security.jwt.access-token.secret") String accessTokenSecret,
            @Value("security.jwt.refresh-token.secret") String refreshTokenSecret) {
        this.ACCESS_TOKEN_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessTokenSecret));
        this.REFRESH_TOKEN_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshTokenSecret));
    }

    public TokenStatus validateAccessToken(String token) {
        return validate(token, ACCESS_TOKEN_SECRET);
    }

    public TokenStatus validateRefreshToken(String token) {
        return validate(token, REFRESH_TOKEN_SECRET);
    }

    private TokenStatus validate(String token, SecretKey key) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            log.info("Token is valid.");
            return TokenStatus.OK;
        } catch (ExpiredJwtException expEx) {
            log.warn("Token is expired. {}", token);
            return TokenStatus.EXPIRED;
        } catch (UnsupportedJwtException unsEx) {
            log.warn("Token is unsupported. {}", token);
            return TokenStatus.UNSUPPORTED;
        } catch (MalformedJwtException mjEx) {
            log.warn("Token is malformed. {}", token);
            return TokenStatus.MALFORMED;
        } catch (SignatureException sEx) {
            log.warn("Token has a wrong signature. {}", token);
            return TokenStatus.WRONG_SIGNATURE;
        } catch (Exception e) {
            log.warn("Token invalid. {}", token);
            return TokenStatus.INVALID;
        }
    }

}
