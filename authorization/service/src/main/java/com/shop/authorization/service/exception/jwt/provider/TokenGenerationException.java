package com.shop.authorization.service.exception.jwt.provider;

import com.shop.authorization.service.exception.jwt.JwtServiceException;

public class TokenGenerationException extends JwtServiceException {

    public TokenGenerationException() {
    }

    public TokenGenerationException(String message) {
        super(message);
    }

    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenGenerationException(Throwable cause) {
        super(cause);
    }
}
