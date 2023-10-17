package com.shop.authorization.service.exception.validation;

import com.shop.common.utils.exception.regex.RegExValidationException;

public class EmailValidationException extends RegExValidationException {

    public EmailValidationException() {
    }

    public EmailValidationException(String message) {
        super(message);
    }

    public EmailValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailValidationException(Throwable cause) {
        super(cause);
    }
}
