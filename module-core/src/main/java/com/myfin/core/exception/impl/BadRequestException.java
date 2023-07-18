package com.myfin.core.exception.impl;

import com.myfin.core.BaseAbstractRestApiException;

public class BadRequestException extends BaseAbstractRestApiException {

    private final String errorMessage;

    public BadRequestException(String message) {
        super(message);
        this.errorMessage = message;
    }

    @Override
    public int getHttpStatus() {
        return 400;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
