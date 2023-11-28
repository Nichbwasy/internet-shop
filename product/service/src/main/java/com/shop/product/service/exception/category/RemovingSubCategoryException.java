package com.shop.product.service.exception.category;

import com.shop.common.utils.all.exception.service.CommonServiceException;

public class RemovingSubCategoryException extends CommonServiceException {

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
