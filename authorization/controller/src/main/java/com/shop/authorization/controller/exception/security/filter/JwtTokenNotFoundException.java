package com.shop.authorization.controller.exception.security.filter;

public class JwtTokenNotFoundException extends JwtFilterSecurityException {

    public JwtTokenNotFoundException() {
    }

    public JwtTokenNotFoundException(String message) {
        super(message);
    }

    public JwtTokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenNotFoundException(Throwable cause) {
        super(cause);
    }
}
