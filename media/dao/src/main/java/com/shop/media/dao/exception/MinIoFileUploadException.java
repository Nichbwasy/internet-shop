package com.shop.media.dao.exception;

public class MinIoFileUploadException extends MinIoStorageException {
    public MinIoFileUploadException() {
    }

    public MinIoFileUploadException(String message) {
        super(message);
    }

    public MinIoFileUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinIoFileUploadException(Throwable cause) {
        super(cause);
    }
}
