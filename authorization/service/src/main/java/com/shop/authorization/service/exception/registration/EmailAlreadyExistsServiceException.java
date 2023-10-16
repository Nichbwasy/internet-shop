package com.shop.authorization.service.exception.registration;

import com.shop.common.utils.exception.service.ServiceException;

public class EmailAlreadyExistsServiceException extends ServiceException {

    public EmailAlreadyExistsServiceException() {
    }

    public EmailAlreadyExistsServiceException(String message) {
        super(message);
    }

    public EmailAlreadyExistsServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailAlreadyExistsServiceException(Throwable cause) {
        super(cause);
    }
}
