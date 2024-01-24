package com.shop.media.service.exeption;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class FileReadingException extends CommonServiceException {

    public FileReadingException() {
    }

    public FileReadingException(String message) {
        super(message);
    }

    public FileReadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileReadingException(Throwable cause) {
        super(cause);
    }
}
