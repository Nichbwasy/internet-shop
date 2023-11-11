package com.shop.common.utils.exception.jwt.token;

import com.shop.common.utils.exception.jwt.JwtTokenValidationException;

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
