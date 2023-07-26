package com.myfin.core.exception.impl;

import com.myfin.core.AbstractRestApiException;

public class ForbiddenException extends AbstractRestApiException {

    private final String errorMessage;

    public ForbiddenException(String message) {
        super(message);
        this.errorMessage = message;
    }

    @Override
    public int getHttpStatus() {
        return 403;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
