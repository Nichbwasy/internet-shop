package com.shop.shop.service.exception.shop;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class ProductsNotFoundForFilterException extends CommonServiceException {

    public ProductsNotFoundForFilterException() {
    }

    public ProductsNotFoundForFilterException(String message) {
        super(message);
    }

    public ProductsNotFoundForFilterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductsNotFoundForFilterException(Throwable cause) {
        super(cause);
    }
}
