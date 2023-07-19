package com.myfin.core.exception.impl;

import com.myfin.core.AbstractRestApiException;

public class UnauthorizedException extends AbstractRestApiException {

    private final String message;

    public UnauthorizedException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public int getHttpStatus() {
        return 401;
    }

    @Override
    public String getErrorMessage() {
        return this.message;
    }
}
