package com.shop.authorization.service.exception.token;

public class TokensNotMatchException extends TokenServiceException {

    public TokensNotMatchException() {
    }

    public TokensNotMatchException(String message) {
        super(message);
    }

    public TokensNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokensNotMatchException(Throwable cause) {
        super(cause);
    }
}
