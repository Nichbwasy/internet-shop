package com.shop.common.utils.exception.jwt;

public class AccessTokenInvalidSecurityException extends JwtTokenValidationException {

    public AccessTokenInvalidSecurityException() {
    }

    public AccessTokenInvalidSecurityException(String message) {
        super(message);
    }

    public AccessTokenInvalidSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessTokenInvalidSecurityException(Throwable cause) {
        super(cause);
    }
}
