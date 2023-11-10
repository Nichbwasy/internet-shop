package com.shop.common.utils.exception.jwt.token;

import com.shop.common.utils.exception.jwt.JwtTokenValidationException;

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
