package com.myfin.core.exception;

import com.myfin.core.AbstractRestApiException;
import com.myfin.core.util.SeoulDateTime;
import lombok.*;
import org.springframework.http.HttpStatusCode;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ErrorResponse {

    private String timestamp;
    private int httpStatus;
    private String errorMessage;
    private String path;

    public static ErrorResponse errorResponse(AbstractRestApiException ex, String path) {
        return ErrorResponse.builder()
                .timestamp(SeoulDateTime.now().toString())
                .httpStatus(ex.getHttpStatus())
                .errorMessage(ex.getErrorMessage())
                .path(path)
                .build();
    }

    public static ErrorResponse errorResponse(HttpStatusCode statusCode, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(SeoulDateTime.now().toString())
                .httpStatus(statusCode.value())
                .errorMessage(message)
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
