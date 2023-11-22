package com.shop.product.service.exception.category;

import com.shop.common.utils.all.exception.service.ServiceException;

public class RemovingSubCategoryException extends ServiceException {

    public RemovingSubCategoryException() {
    }

    public RemovingSubCategoryException(String message) {
        super(message);
    }

    public RemovingSubCategoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemovingSubCategoryException(Throwable cause) {
        super(cause);
    }
}
