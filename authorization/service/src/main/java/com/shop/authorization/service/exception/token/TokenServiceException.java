package com.shop.authorization.service.exception.token;

import com.shop.common.utils.exception.service.ServiceException;

public class TokenServiceException extends ServiceException {

    public TokenServiceException() {
    }

    public TokenServiceException(String message) {
        super(message);
    }

    public TokenServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenServiceException(Throwable cause) {
        super(cause);
    }
}
