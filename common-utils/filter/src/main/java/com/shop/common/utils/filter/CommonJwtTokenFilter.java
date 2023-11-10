package com.shop.common.utils.filter;

import com.shop.authorization.client.TokensApiClient;
import com.shop.authorization.common.constant.jwt.TokenStatus;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.common.utils.exception.jwt.AccessTokenInvalidSecurityException;
import com.shop.common.utils.exception.jwt.AccessTokenNotFoundSecurityException;
import com.shop.common.utils.exception.jwt.JwtTokenValidationException;
import com.shop.common.utils.exception.jwt.RefreshTokenNotFoundSecurityException;
import com.shop.common.utils.exception.jwt.token.JwtTokenInvalidException;
import com.shop.common.utils.exception.jwt.token.JwtTokenMalformedException;
import com.shop.common.utils.exception.jwt.token.JwtTokenUnsupportedException;
import com.shop.common.utils.exception.jwt.token.JwtTokenWrongSignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CommonJwtTokenFilter extends GenericFilterBean {
    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String REFRESH_HEADER = "Refresh";
    private final static String BEARER = "Bearer ";

    private final TokensApiClient tokensApiClient;

    private List<String> filteredPaths;

    public CommonJwtTokenFilter(TokensApiClient client) {
        this.tokensApiClient = client;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String path = httpServletRequest.getServletPath();

        if (checkFilteredPaths(path)) {
            log.info("Common jwt token filter ignores the path '{}'.", path);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String accessToken = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
        String refreshToken = httpServletRequest.getHeader(REFRESH_HEADER);

        validateAccessToken(accessToken);

        accessToken = accessToken.substring(BEARER.length());
        TokenStatus status = tokensApiClient.validateAccessToken(accessToken);

        switch (status) {
            case OK -> log.info("Token is valid! ");
            case EXPIRED -> {
                if (refreshToken == null) {
                    log.warn("Refresh token not found in request! Unable refresh access/refresh tokens!");
                    throw new RefreshTokenNotFoundSecurityException(
                            "Refresh token not found in request! Unable refresh access/refresh tokens!"
                    );
                }
                AccessRefreshTokens tokens = tokensApiClient.updateTokens(refreshToken);
                accessToken = tokens.getAccessToken();
                refreshToken = tokens.getRefreshToken();
            } default -> resolveTokenStatus(status);
        }

        // TODO: Need to be fixed
        UsernamePasswordAuthenticationToken authentication = tokensApiClient.getAuthenticationFromToken(accessToken);
        httpServletResponse.addHeader(AUTHORIZATION_HEADER, accessToken);
        httpServletResponse.addHeader(REFRESH_HEADER, refreshToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private static void validateAccessToken(String accessToken) {
        if (accessToken == null) {
            log.warn("Access token not found!");
            throw new AccessTokenNotFoundSecurityException("Authorization token not found!");
        }
        if (!accessToken.startsWith(BEARER)) {
            log.warn("Access token has wrong format!");
            throw new AccessTokenInvalidSecurityException("Access token has wrong format!");
        }
    }

    private void resolveTokenStatus(TokenStatus status) {
        log.warn("Token is not valid! Trying to resolve token status...");
        switch (status) {
            case UNSUPPORTED -> throw new JwtTokenUnsupportedException("Unable validate token! Token unsupported!");
            case MALFORMED -> throw new JwtTokenMalformedException("Unable validate token! Token malformed!");
            case WRONG_SIGNATURE -> throw new JwtTokenWrongSignatureException("Unable validate token! Token has a wrong signature!");
            case INVALID -> throw new JwtTokenInvalidException("Unable validate token! Token is invalid!");
            default -> throw new JwtTokenValidationException("Unable validate token! Unknown token status!");
        }
    }

    private Boolean checkFilteredPaths(String url) {
        return filteredPaths.stream().anyMatch(
                path -> {
                    if (path.contains("/**")) return url.startsWith(path.replace("/**", ""));
                    else return url.equals(path);
                }
        );
    }

    public void setFilteredPaths(String... filteredPaths) {
        this.filteredPaths = Arrays.asList(filteredPaths);
    }
}
