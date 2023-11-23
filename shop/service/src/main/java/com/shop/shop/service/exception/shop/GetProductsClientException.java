package com.shop.shop.service.exception.shop;

import com.shop.common.utils.all.exception.client.CommonMicroserviceClientException;

public class GetProductsClientException extends CommonMicroserviceClientException {

    public GetProductsClientException() {
    }

    public GetProductsClientException(String message) {
        super(message);
    }

    public GetProductsClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetProductsClientException(Throwable cause) {
        super(cause);
    }
}
