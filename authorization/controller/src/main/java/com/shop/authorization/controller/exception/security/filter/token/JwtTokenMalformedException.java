package com.shop.authorization.controller.exception.security.filter.token;

import com.shop.authorization.controller.exception.security.filter.JwtTokenValidationException;

public class JwtTokenMalformedException extends JwtTokenValidationException {
    public JwtTokenMalformedException() {
    }

    public JwtTokenMalformedException(String message) {
        super(message);
    }

    public JwtTokenMalformedException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenMalformedException(Throwable cause) {
        super(cause);
    }
}
