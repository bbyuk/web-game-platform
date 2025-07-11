package com.bb.webcanvasservice.infrastructure.exception.model.http;

import java.time.LocalDateTime;

/**
 * Rest API 공통 예외처리 시 리턴하는 응답 객체
 * @param timestamp
 * @param message
 * @param path
 */
public record ExceptionResponse(
        LocalDateTime timestamp,
        String code,
        String message,
        String path
) {
}
