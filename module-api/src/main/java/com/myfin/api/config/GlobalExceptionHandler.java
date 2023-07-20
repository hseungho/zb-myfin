package com.myfin.api.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.myfin.core.AbstractRestApiException;
import com.myfin.core.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AbstractRestApiException.class)
    public ResponseEntity<ErrorResponse> handleRestException(AbstractRestApiException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ErrorResponse.errorResponse(ex, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Occurred MethodArgumentNotValidException.");
        return ResponseEntity.status(ex.getStatusCode())
                .body(ErrorResponse.errorResponse(ex.getStatusCode(), ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        if (ex.getCause() instanceof InvalidFormatException ife) {
            if (ife.getCause() instanceof DateTimeParseException) {
                log.error("Occurred DateTimeParseException.");
                return ResponseEntity.status(400)
                        .body(ErrorResponse.errorResponse(HttpStatus.BAD_REQUEST, "날짜 또는 시간 형식에 맞게 입력해주세요", request.getRequestURI()));
            }
        }

        log.error("Occurred HttpMessageNotReadableException.");
        return ResponseEntity.status(400)
                .body(ErrorResponse.errorResponse(HttpStatus.BAD_REQUEST, "올바른 형식의 요청값을 입력해주세요", request.getRequestURI()));
    }

}
