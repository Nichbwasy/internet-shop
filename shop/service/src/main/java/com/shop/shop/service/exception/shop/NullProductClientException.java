package com.shop.shop.service.exception.shop;

import com.shop.common.utils.all.exception.client.CommonMicroserviceClientException;

public class NullProductClientException extends CommonMicroserviceClientException {

    public NullProductClientException() {
    }

    public NullProductClientException(String message) {
        super(message);
    }

    public NullProductClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullProductClientException(Throwable cause) {
        super(cause);
    }
}
