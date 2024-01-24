package com.shop.media.service.exeption.product;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class ProductMediaAlreadyExistsException extends CommonServiceException {

    public ProductMediaAlreadyExistsException() {
    }

    public ProductMediaAlreadyExistsException(String message) {
        super(message);
    }

    public ProductMediaAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductMediaAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
