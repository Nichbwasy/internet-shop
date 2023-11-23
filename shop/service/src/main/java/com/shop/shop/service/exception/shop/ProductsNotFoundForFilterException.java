package com.shop.shop.service.exception.shop;

import com.shop.common.utils.all.exception.service.ServiceException;

public class ProductsNotFoundForFilterException extends ServiceException {

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
