package com.shop.authorization.service.exception.jwt.provider;

import com.shop.authorization.service.exception.jwt.JwtServiceException;

public class JwtTokenGenerationException extends JwtServiceException {

    public JwtTokenGenerationException() {
    }

    public JwtTokenGenerationException(String message) {
        super(message);
    }

    public JwtTokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenGenerationException(Throwable cause) {
        super(cause);
    }
}
