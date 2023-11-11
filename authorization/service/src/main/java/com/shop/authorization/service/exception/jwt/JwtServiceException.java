package com.shop.authorization.service.exception.jwt;

import com.shop.common.utils.exception.service.ServiceException;

public class JwtServiceException extends ServiceException {

    public JwtServiceException() {
    }

    public JwtServiceException(String message) {
        super(message);
    }

    public JwtServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtServiceException(Throwable cause) {
        super(cause);
    }
}
