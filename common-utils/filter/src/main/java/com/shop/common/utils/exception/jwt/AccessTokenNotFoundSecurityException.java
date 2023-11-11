package com.shop.common.utils.exception.jwt;

public class AccessTokenNotFoundSecurityException extends JwtTokenNotFoundException {

    public AccessTokenNotFoundSecurityException() {
    }

    public AccessTokenNotFoundSecurityException(String message) {
        super(message);
    }

    public AccessTokenNotFoundSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessTokenNotFoundSecurityException(Throwable cause) {
        super(cause);
    }
}
