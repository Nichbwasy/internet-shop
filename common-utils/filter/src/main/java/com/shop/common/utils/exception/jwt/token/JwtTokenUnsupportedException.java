package com.shop.common.utils.exception.jwt.token;

import com.shop.common.utils.exception.jwt.JwtTokenValidationException;
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
