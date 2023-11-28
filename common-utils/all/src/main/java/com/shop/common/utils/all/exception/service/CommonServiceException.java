package com.shop.common.utils.all.exception.service;

public class CommonServiceException extends RuntimeException {
    public CommonServiceException() {
    }

    public CommonServiceException(String message) {
        super(message);
    }

    public CommonServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonServiceException(Throwable cause) {
        super(cause);
    }

}
