package com.shop.authorization.controller.exception.security.filter.token;

import com.shop.authorization.controller.exception.security.filter.JwtTokenValidationException;

public class JwtTokenExpiredException extends JwtTokenValidationException {

    public JwtTokenExpiredException() {
    }

    public JwtTokenExpiredException(String message) {
        super(message);
    }

    public JwtTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenExpiredException(Throwable cause) {
        super(cause);
    }
}
