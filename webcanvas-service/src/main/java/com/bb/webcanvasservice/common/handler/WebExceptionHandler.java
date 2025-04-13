package com.bb.webcanvasservice.common.handler;

import com.bb.webcanvasservice.common.code.ErrorCode;
import com.bb.webcanvasservice.common.exception.BusinessException;
import com.bb.webcanvasservice.common.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * RestAPI 처리 중 HandlerMapping 이후에 발생하는 예외 공통 처리를 위한 클래스
 */
@RestControllerAdvice
public class WebExceptionHandler {

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
}
