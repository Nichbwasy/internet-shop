package com.shop.authorization.service.exception.jwt.util;

import com.shop.authorization.service.exception.jwt.JwtServiceException;

public class JwtTokenUtilsException extends JwtServiceException {

    public JwtTokenUtilsException() {
    }

    public JwtTokenUtilsException(String message) {
        super(message);
    }

    public JwtTokenUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenUtilsException(Throwable cause) {
        super(cause);
    }
}
