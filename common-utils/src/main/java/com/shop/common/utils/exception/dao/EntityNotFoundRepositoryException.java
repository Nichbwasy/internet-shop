package com.shop.common.utils.exception.dao;

public class EntityNotFoundRepositoryException extends RepositoryException {
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
