package com.shop.media.dao.exception;

public class MinIoStorageException extends RuntimeException {

    public MinIoStorageException() {
    }

    public MinIoStorageException(String message) {
        super(message);
    }

    public MinIoStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinIoStorageException(Throwable cause) {
        super(cause);
    }
}
