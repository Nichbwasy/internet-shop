package com.shop.authorization.service.exception.registration;

import com.shop.common.utils.exception.service.ServiceException;

public class PasswordsNotMatchRegistrationException extends ServiceException {
    public PasswordsNotMatchRegistrationException() {
    }

    public PasswordsNotMatchRegistrationException(String message) {
        super(message);
    }

    public PasswordsNotMatchRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordsNotMatchRegistrationException(Throwable cause) {
        super(cause);
    }
}
