package com.shop.shop.service.exception.shop;

import com.shop.common.utils.all.exception.client.CommonMicroserviceClientException;

public class GetProductClientException extends CommonMicroserviceClientException {

    public GetProductClientException() {
    }

    public GetProductClientException(String message) {
        super(message);
    }

    public GetProductClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetProductClientException(Throwable cause) {
        super(cause);
    }
}
