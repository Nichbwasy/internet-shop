package com.shop.common.utils.all.exception.dao;

public class EntityGetRepositoryException extends CommonRepositoryException {

    public EntityGetRepositoryException() {
    }

    public EntityGetRepositoryException(String message) {
        super(message);
    }

    public EntityGetRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityGetRepositoryException(Throwable cause) {
        super(cause);
    }
}
