package com.bb.webcanvasservice.common.dto;

import java.time.LocalDateTime;

/**
 * Rest API 공통 예외처리 시 리턴하는 응답 객체
 * @param timestamp
 * @param message
 * @param path
 */
public record ExceptionResponse(
        LocalDateTime timestamp,
        String message,
        String path
) {
}
