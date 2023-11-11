package com.shop.authorization.controller.exception.security.filter.token;

import com.shop.authorization.controller.exception.security.filter.JwtTokenValidationException;

public class JwtTokenInvalidException extends JwtTokenValidationException {

    public JwtTokenInvalidException() {
    }

    public JwtTokenInvalidException(String message) {
        super(message);
    }

    public JwtTokenInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenInvalidException(Throwable cause) {
        super(cause);
    }
}
