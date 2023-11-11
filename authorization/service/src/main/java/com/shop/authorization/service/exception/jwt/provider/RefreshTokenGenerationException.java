package com.shop.authorization.service.exception.jwt.provider;

public class RefreshTokenGenerationException extends JwtTokenGenerationException {
    public RefreshTokenGenerationException() {
    }

    public RefreshTokenGenerationException(String message) {
        super(message);
    }

    public RefreshTokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenGenerationException(Throwable cause) {
        super(cause);
    }
}
