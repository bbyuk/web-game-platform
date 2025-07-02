package com.bb.webcanvasservice.infrastructure.exception.handler.http;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;
import com.bb.webcanvasservice.infrastructure.exception.model.http.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * RestAPI 처리 중 HandlerMapping 이후에 발생하는 예외 공통 처리를 위한 클래스
 */
@Slf4j
@RestControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ExceptionResponse(
                        LocalDateTime.now(),
                        errorCode.getCode(),
                        e.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.SYSTEM_ERROR;

        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(new ExceptionResponse(
                        LocalDateTime.now(),
                        errorCode.getCode(),
                        errorCode.getDefaultMessage(),
                        request.getRequestURI()));
    }
}
