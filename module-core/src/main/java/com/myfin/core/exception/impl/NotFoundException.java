package com.myfin.core.exception.impl;

import com.myfin.core.AbstractRestApiException;

public class NotFoundException extends AbstractRestApiException {

    private final String errorMessage;

    public NotFoundException(String message) {
        super(message);
        this.errorMessage = message;
    }
    @Override
    public int getHttpStatus() {
        return 404;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
