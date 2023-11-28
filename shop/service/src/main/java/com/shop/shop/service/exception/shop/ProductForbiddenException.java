package com.shop.shop.service.exception.shop;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class ProductForbiddenException extends CommonServiceException {

    public ProductForbiddenException() {
    }

    public ProductForbiddenException(String message) {
        super(message);
    }

    public ProductForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductForbiddenException(Throwable cause) {
        super(cause);
    }
}
