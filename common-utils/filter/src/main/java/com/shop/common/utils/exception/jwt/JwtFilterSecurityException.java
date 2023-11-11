package com.shop.common.utils.exception.jwt;

public class JwtFilterSecurityException extends RuntimeException {

    public JwtFilterSecurityException() {
    }

    public JwtFilterSecurityException(String message) {
        super(message);
    }

    public JwtFilterSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtFilterSecurityException(Throwable cause) {
        super(cause);
    }

}
