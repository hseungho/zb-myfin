package com.myfin.api.config;

import com.myfin.core.AbstractRestApiException;
import com.myfin.core.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(AbstractRestApiException.class)
    public ResponseEntity<ErrorResponse> handleRestException(AbstractRestApiException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ErrorResponse.errorResponse(ex, request.getRequestURI()));
    }
}
