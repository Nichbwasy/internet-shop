package com.shop.authorization.service.exception.jwt.provider;

public class AccessTokenGenerationException extends TokenGenerationException {
    public AccessTokenGenerationException() {
    }

    public AccessTokenGenerationException(String message) {
        super(message);
    }

    public AccessTokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessTokenGenerationException(Throwable cause) {
        super(cause);
    }
}
