package com.shop.common.utils.all.exception.dao;

public class EntityUpdateRepositoryException extends CommonRepositoryException {
    public EntityUpdateRepositoryException() {
    }

    public EntityUpdateRepositoryException(String message) {
        super(message);
    }

    public EntityUpdateRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityUpdateRepositoryException(Throwable cause) {
        super(cause);
    }
}
