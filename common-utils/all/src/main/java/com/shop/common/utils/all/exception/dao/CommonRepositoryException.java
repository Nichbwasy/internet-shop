package com.shop.common.utils.all.exception.dao;

public class CommonRepositoryException extends RuntimeException {
    public CommonRepositoryException() {
    }

    public CommonRepositoryException(String message) {
        super(message);
    }

    public CommonRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonRepositoryException(Throwable cause) {
        super(cause);
    }

}
