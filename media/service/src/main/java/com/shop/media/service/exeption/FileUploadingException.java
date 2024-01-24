package com.shop.media.service.exeption;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class FileUploadingException extends CommonServiceException {

    public FileUploadingException() {
    }

    public FileUploadingException(String message) {
        super(message);
    }

    public FileUploadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileUploadingException(Throwable cause) {
        super(cause);
    }
}
