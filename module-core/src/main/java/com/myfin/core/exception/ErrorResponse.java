package com.myfin.core.exception;

import com.myfin.core.BaseAbstractRestApiException;
import com.myfin.core.util.SeoulDateTime;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ErrorResponse {

    private String timestamp;
    private int httpStatus;
    private String errorMessage;
    private String path;

    public static ErrorResponse errorResponse(BaseAbstractRestApiException ex, String path) {
        return ErrorResponse.builder()
                .timestamp(SeoulDateTime.now().toString())
                .httpStatus(ex.getHttpStatus())
                .errorMessage(ex.getErrorMessage())
                .path(path)
                .build();
    }

    @Override
    public String toString() {
        return "{" +
                "timestamp=" + timestamp +
                ", httpStatus=" + httpStatus +
                ", errorMessage='" + errorMessage + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
