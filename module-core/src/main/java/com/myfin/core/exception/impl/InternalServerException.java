package com.myfin.core.exception.impl;

import com.myfin.core.AbstractRestApiException;

public class InternalServerException extends AbstractRestApiException {

    private final String errorMessage;

    public InternalServerException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    @Override
    public int getHttpStatus() {
        return 500;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
