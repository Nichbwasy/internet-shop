package com.shop.common.utils.all.exception.dao;

public class EntityNotFoundRepositoryException extends CommonRepositoryException {
    public EntityNotFoundRepositoryException() {
    }

    public EntityNotFoundRepositoryException(String message) {
        super(message);
    }

    public EntityNotFoundRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundRepositoryException(Throwable cause) {
        super(cause);
    }
}
