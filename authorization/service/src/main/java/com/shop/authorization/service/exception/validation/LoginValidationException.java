package com.shop.authorization.service.exception.validation;

import com.shop.common.utils.exception.regex.RegExValidationException;

public class LoginValidationException extends RegExValidationException {
    public LoginValidationException() {}

    public LoginValidationException(String message) {
        super(message);
    }

    public LoginValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginValidationException(Throwable cause) {
        super(cause);
    }
}
