package com.shop.authorization.controller.exception.security.filter.token;

import com.shop.authorization.controller.exception.security.filter.JwtTokenValidationException;

public class JwtTokenUnsupportedException extends JwtTokenValidationException {

    public JwtTokenUnsupportedException() {
    }

    public JwtTokenUnsupportedException(String message) {
        super(message);
    }

    public JwtTokenUnsupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenUnsupportedException(Throwable cause) {
        super(cause);
    }
}
