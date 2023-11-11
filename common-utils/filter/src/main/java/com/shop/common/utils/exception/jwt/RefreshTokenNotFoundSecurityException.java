package com.shop.common.utils.exception.jwt;

public class RefreshTokenNotFoundSecurityException extends JwtTokenNotFoundException {

    public RefreshTokenNotFoundSecurityException() {
    }

    public RefreshTokenNotFoundSecurityException(String message) {
        super(message);
    }

    public RefreshTokenNotFoundSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenNotFoundSecurityException(Throwable cause) {
        super(cause);
    }
}
