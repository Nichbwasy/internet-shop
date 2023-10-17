package com.shop.authorization.service.exception.authorization;

import com.shop.common.utils.exception.service.ServiceException;

public class UserNotFoundAuthorizationException extends ServiceException {

    public UserNotFoundAuthorizationException() {
    }

    public UserNotFoundAuthorizationException(String message) {
        super(message);
    }

    public UserNotFoundAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundAuthorizationException(Throwable cause) {
        super(cause);
    }
}
