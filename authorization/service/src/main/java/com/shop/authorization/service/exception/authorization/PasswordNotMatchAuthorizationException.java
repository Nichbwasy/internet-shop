package com.shop.authorization.service.exception.authorization;

import com.shop.common.utils.exception.service.ServiceException;

public class PasswordNotMatchAuthorizationException extends ServiceException {

    public PasswordNotMatchAuthorizationException() {
    }

    public PasswordNotMatchAuthorizationException(String message) {
        super(message);
    }

    public PasswordNotMatchAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordNotMatchAuthorizationException(Throwable cause) {
        super(cause);
    }
}
