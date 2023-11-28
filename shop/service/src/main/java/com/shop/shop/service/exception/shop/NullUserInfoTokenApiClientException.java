package com.shop.shop.service.exception.shop;

import com.shop.common.utils.all.exception.client.CommonMicroserviceClientException;

public class NullUserInfoTokenApiClientException extends CommonMicroserviceClientException {

    public NullUserInfoTokenApiClientException() {
    }

    public NullUserInfoTokenApiClientException(String message) {
        super(message);
    }

    public NullUserInfoTokenApiClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullUserInfoTokenApiClientException(Throwable cause) {
        super(cause);
    }
}
