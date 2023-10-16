package com.shop.authorization.service.exception.registration;

import com.shop.common.utils.exception.service.ServiceException;

public class PasswordsNotMatchServiceException extends ServiceException {
    public PasswordsNotMatchServiceException() {
    }

    public PasswordsNotMatchServiceException(String message) {
        super(message);
    }

    public PasswordsNotMatchServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordsNotMatchServiceException(Throwable cause) {
        super(cause);
    }
}
