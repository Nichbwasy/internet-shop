package com.shop.common.utils.exception.jwt.token;


import com.shop.common.utils.exception.jwt.JwtTokenValidationException;

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
