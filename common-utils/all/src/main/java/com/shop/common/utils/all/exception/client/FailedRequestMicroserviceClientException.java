package com.shop.common.utils.all.exception.client;

public class FailedRequestMicroserviceClientException extends CommonMicroserviceClientException {
    public FailedRequestMicroserviceClientException() {
    }

    public FailedRequestMicroserviceClientException(String message) {
        super(message);
    }

    public FailedRequestMicroserviceClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedRequestMicroserviceClientException(Throwable cause) {
        super(cause);
    }
}
