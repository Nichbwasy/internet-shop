package com.shop.shop.service.exception.shop;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class ProductNotApprovedException extends CommonServiceException {

    public ProductNotApprovedException() {
    }

    public ProductNotApprovedException(String message) {
        super(message);
    }

    public ProductNotApprovedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductNotApprovedException(Throwable cause) {
        super(cause);
    }
}
