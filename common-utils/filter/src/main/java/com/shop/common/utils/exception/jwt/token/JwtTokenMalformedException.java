package com.shop.common.utils.exception.jwt.token;

import com.shop.common.utils.exception.jwt.JwtTokenValidationException;

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
