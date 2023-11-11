package com.shop.authorization.controller.exception.security.filter;

public class JwtTokenValidationException extends JwtFilterSecurityException {

    public JwtTokenValidationException() {
    }

    public JwtTokenValidationException(String message) {
        super(message);
    }

    public JwtTokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenValidationException(Throwable cause) {
        super(cause);
    }
}
