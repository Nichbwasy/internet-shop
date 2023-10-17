package com.shop.authorization.service.exception.registration;

import com.shop.common.utils.exception.service.ServiceException;

public class EmailAlreadyExistsRegistrationException extends ServiceException {

    public EmailAlreadyExistsRegistrationException() {
    }

    public EmailAlreadyExistsRegistrationException(String message) {
        super(message);
    }

    public EmailAlreadyExistsRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailAlreadyExistsRegistrationException(Throwable cause) {
        super(cause);
    }
}
