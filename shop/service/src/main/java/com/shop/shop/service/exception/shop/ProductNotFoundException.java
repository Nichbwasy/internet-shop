package com.shop.shop.service.exception.shop;

import com.shop.common.utils.all.exception.client.CommonMicroserviceClientException;

public class ProductNotFoundException extends CommonMicroserviceClientException {

    public ProductNotFoundException() {
    }

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductNotFoundException(Throwable cause) {
        super(cause);
    }
}
