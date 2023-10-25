package com.shop.authorization.controller.security.filter;

import com.shop.authorization.controller.exception.security.filter.JwtTokenNotFoundException;
import com.shop.authorization.controller.exception.security.filter.JwtTokenValidationException;
import com.shop.authorization.controller.exception.security.filter.token.JwtTokenInvalidException;
import com.shop.authorization.controller.exception.security.filter.token.JwtTokenMalformedException;
import com.shop.authorization.controller.exception.security.filter.token.JwtTokenUnsupportedException;
import com.shop.authorization.controller.exception.security.filter.token.JwtTokenWrongSignatureException;
import com.shop.authorization.controller.security.dto.JwtAuthenticationDto;
import com.shop.authorization.dto.token.AccessRefreshTokens;
import com.shop.authorization.service.TokensService;
import com.shop.authorization.service.jwt.utils.JwtTokenUtils;
import com.shop.authorization.service.jwt.validator.JwtTokenValidator;
import com.shop.authorization.service.jwt.validator.TokenStatus;
import io.jsonwebtoken.Claims;
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
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenUtils jwtTokenUtils;
    private final TokensService tokensService;

    public JwtTokenFilter(JwtTokenValidator jwtTokenValidator, JwtTokenUtils jwtTokenUtils, TokensService tokensService) {
        this.jwtTokenValidator = jwtTokenValidator;
        this.jwtTokenUtils = jwtTokenUtils;
        this.tokensService = tokensService;
    }

    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String REFRESH_HEADER = "Refresh";
    private final static String BEARER = "Bearer ";
    private List<String> filteredPaths;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) servletRequest);
        HttpServletResponse httpServletResponse = ((HttpServletResponse) servletResponse);

        String path = httpServletRequest.getServletPath();
        if (checkFilteredPaths(path)) {
            log.info("Filter ignore request's path '{}'.", path);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        resolveTokens(httpServletRequest, httpServletResponse);

        filterChain.doFilter(servletRequest, servletResponse);

    }

    private void resolveTokens(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String accessToken = getAccessTokenFromRequest(httpServletRequest);
        String refreshToken = getRefreshTokenFromRequest(httpServletRequest);

        TokenStatus status = jwtTokenValidator.validateAccessToken(accessToken);
        switch (status) {
            case OK -> {
                log.info("Access token is valid.");
                UsernamePasswordAuthenticationToken authentication = getAuthenticationFromAccessToken(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            case EXPIRED -> {
                log.info("Access token is expired. Trying to refresh token...");
                AccessRefreshTokens tokens = tokensService.refreshTokens(refreshToken);
                UsernamePasswordAuthenticationToken authentication = getAuthenticationFromAccessToken(tokens.getAccessToken());

                httpServletResponse.addHeader(AUTHORIZATION_HEADER, tokens.getAccessToken());
                httpServletResponse.addHeader(REFRESH_HEADER, tokens.getRefreshToken());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            default -> resolveTokenStatus(status);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthenticationFromAccessToken(String accessToken) {
        Claims claims = jwtTokenUtils.getAccessTokenClaims(accessToken);
        JwtAuthenticationDto authenticationDto = new JwtAuthenticationDto(claims);
        return new UsernamePasswordAuthenticationToken(
                authenticationDto.getUsername(),
                authenticationDto.getCredentials(),
                authenticationDto.getAuthorities()
        );
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

    private String getAccessTokenFromRequest(HttpServletRequest httpServletRequest) {
        String accessTokenHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
        if (accessTokenHeader == null) {
            log.warn("Access token not found in request!");
            throw new JwtTokenNotFoundException("Access token not found in request!");
        }
        if (!accessTokenHeader.startsWith(BEARER)) {
            log.warn("Unable get authorization token! Invalid access token format!");
            throw new JwtTokenValidationException("Unable get authorization token! Invalid access token format!");
        }
        return accessTokenHeader.substring(BEARER.length());
    }

    private String getRefreshTokenFromRequest(HttpServletRequest httpServletRequest) {
        String refreshTokenHeader = httpServletRequest.getHeader(REFRESH_HEADER);
        if (refreshTokenHeader == null) {
            log.warn("Refresh token not found in request!");
            throw new JwtTokenNotFoundException("Refresh token not found in request!");
        }
        return refreshTokenHeader;
    }

    private Boolean checkFilteredPaths(String url) {
        return filteredPaths.stream().anyMatch(
                path -> {
                    if (path.contains("/**")) return url.startsWith(path.replace("/**", ""));
                    else return url.equals(path);
                }
        );
    }

    public void setFilteredPaths(List<String> filteredPaths) {
        this.filteredPaths = filteredPaths;
    }

    public void setFilteredPaths(String... filteredPaths) {
        this.filteredPaths = Arrays.stream(filteredPaths).toList();
    }
}
