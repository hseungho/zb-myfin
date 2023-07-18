package com.myfin.core;

public abstract class BaseAbstractRestApiException extends RuntimeException {

    public BaseAbstractRestApiException(String errorMessage) {
        super(errorMessage);
    }

    public abstract int getHttpStatus();
    public abstract String getErrorMessage();

}
