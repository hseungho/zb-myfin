package com.myfin.core;

public abstract class AbstractRestApiException extends RuntimeException {

    public AbstractRestApiException(String errorMessage) {
        super(errorMessage);
    }

    public abstract int getHttpStatus();
    public abstract String getErrorMessage();

}
