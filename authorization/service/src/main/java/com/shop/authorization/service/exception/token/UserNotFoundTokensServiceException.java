package com.shop.authorization.service.exception.token;

public class UserNotFoundTokensServiceException extends TokenServiceException {
    public UserNotFoundTokensServiceException() {
    }

    public UserNotFoundTokensServiceException(String message) {
        super(message);
    }

    public UserNotFoundTokensServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundTokensServiceException(Throwable cause) {
        super(cause);
    }
}
