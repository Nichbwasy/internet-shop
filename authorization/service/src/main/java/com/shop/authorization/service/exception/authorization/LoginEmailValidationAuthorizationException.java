package com.shop.authorization.service.exception.authorization;

import com.shop.common.utils.exception.service.ServiceException;

public class LoginEmailValidationAuthorizationException extends ServiceException {

    public LoginEmailValidationAuthorizationException() {
    }

    public LoginEmailValidationAuthorizationException(String message) {
        super(message);
    }

    public LoginEmailValidationAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginEmailValidationAuthorizationException(Throwable cause) {
        super(cause);
    }
}
