package com.shop.product.service.exception.product;

import com.shop.common.utils.all.exception.service.ServiceException;

public class RemovingCategoryException extends ServiceException {

    public RemovingCategoryException() {
    }

    public RemovingCategoryException(String message) {
        super(message);
    }

    public RemovingCategoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemovingCategoryException(Throwable cause) {
        super(cause);
    }
}
