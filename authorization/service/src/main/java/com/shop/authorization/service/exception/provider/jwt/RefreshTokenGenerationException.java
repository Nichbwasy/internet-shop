package com.shop.authorization.service.exception.provider.jwt;

public class RefreshTokenGenerationException extends TokenGenerationException {
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
