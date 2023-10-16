package com.shop.authorization.service.exception.registration;

import com.shop.common.utils.exception.service.ServiceException;

public class LoginAlreadyExistsServiceException extends ServiceException {

    public LoginAlreadyExistsServiceException() {
    }

    public LoginAlreadyExistsServiceException(String message) {
        super(message);
    }

    public LoginAlreadyExistsServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginAlreadyExistsServiceException(Throwable cause) {
        super(cause);
    }
}
