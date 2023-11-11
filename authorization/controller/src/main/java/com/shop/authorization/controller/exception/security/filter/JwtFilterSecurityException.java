package com.shop.authorization.controller.exception.security.filter;

import com.shop.authorization.controller.exception.security.SecurityException;

public class JwtFilterSecurityException extends SecurityException {

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
