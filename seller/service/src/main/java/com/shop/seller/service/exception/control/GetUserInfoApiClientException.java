package com.shop.seller.service.exception.control;

import com.shop.common.utils.all.exception.client.CommonMicroserviceClientException;

public class GetUserInfoApiClientException extends CommonMicroserviceClientException {

    public GetUserInfoApiClientException() {
    }

    public GetUserInfoApiClientException(String message) {
        super(message);
    }

    public GetUserInfoApiClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetUserInfoApiClientException(Throwable cause) {
        super(cause);
    }
}
