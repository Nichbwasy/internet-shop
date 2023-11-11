package com.shop.common.utils.all.exception.client;

public class CommonMicroserviceClientException extends RuntimeException {
    public CommonMicroserviceClientException() {
    }

    public CommonMicroserviceClientException(String message) {
        super(message);
    }

    public CommonMicroserviceClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonMicroserviceClientException(Throwable cause) {
        super(cause);
    }
}
