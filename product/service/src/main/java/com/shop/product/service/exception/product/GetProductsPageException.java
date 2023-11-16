package com.shop.product.service.exception.product;

import com.shop.common.utils.all.exception.service.ServiceException;

public class GetProductsPageException extends ServiceException {

    public GetProductsPageException() {
    }

    public GetProductsPageException(String message) {
        super(message);
    }

    public GetProductsPageException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetProductsPageException(Throwable cause) {
        super(cause);
    }
}
