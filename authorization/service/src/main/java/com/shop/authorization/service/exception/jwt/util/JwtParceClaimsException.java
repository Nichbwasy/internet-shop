package com.shop.authorization.service.exception.jwt.util;

public class JwtParceClaimsException extends JwtTokenUtilsException {

    public JwtParceClaimsException() {
    }

    public JwtParceClaimsException(String message) {
        super(message);
    }

    public JwtParceClaimsException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtParceClaimsException(Throwable cause) {
        super(cause);
    }
}
