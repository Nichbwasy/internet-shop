package com.shop.common.utils.exception.dao;

public class EntityDeleteRepositoryException extends RepositoryException {

    public EntityDeleteRepositoryException() {
    }

    public EntityDeleteRepositoryException(String message) {
        super(message);
    }

    public EntityDeleteRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityDeleteRepositoryException(Throwable cause) {
        super(cause);
    }
}
