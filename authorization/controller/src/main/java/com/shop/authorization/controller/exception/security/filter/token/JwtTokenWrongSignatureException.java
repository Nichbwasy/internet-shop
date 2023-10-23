package com.shop.authorization.controller.exception.security.filter.token;

import com.shop.authorization.controller.exception.security.filter.JwtTokenValidationException;

public class JwtTokenWrongSignatureException extends JwtTokenValidationException {

    public JwtTokenWrongSignatureException() {
    }

    public JwtTokenWrongSignatureException(String message) {
        super(message);
    }

    public JwtTokenWrongSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenWrongSignatureException(Throwable cause) {
        super(cause);
    }
}
