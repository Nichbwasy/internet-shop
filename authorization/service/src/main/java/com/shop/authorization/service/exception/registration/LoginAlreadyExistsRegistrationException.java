package com.shop.authorization.service.exception.registration;

import com.shop.common.utils.exception.service.ServiceException;

public class LoginAlreadyExistsRegistrationException extends ServiceException {

    public LoginAlreadyExistsRegistrationException() {
    }

    public LoginAlreadyExistsRegistrationException(String message) {
        super(message);
    }

    public LoginAlreadyExistsRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginAlreadyExistsRegistrationException(Throwable cause) {
        super(cause);
    }
}
