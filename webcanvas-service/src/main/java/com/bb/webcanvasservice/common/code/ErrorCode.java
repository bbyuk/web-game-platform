package com.bb.webcanvasservice.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * API 처리 중 발생한 예외에 대해 클라이언트로 내려줄 에러 코드
 *
 * === code 필드는 아래 정책에 따라 prefix를 갖는다.
 * A - 인증 관련 에러
 * U - 유저 관련 에러
 * R - 게임 방 및 게임 방 입장 관련 에러
 * G - 게임 관련 에러
 */
@RequiredArgsConstructor
public enum ErrorCode {
    SYSTEM_ERROR("S000", "시스템 오류입니다. 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ACCESS_TOKEN("A000", ErrorCode.UNAUTHORIZED_MESSAGE, HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("A001", ErrorCode.UNAUTHORIZED_MESSAGE, HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("A002",ErrorCode.ACCESS_DENIED_MESSAGE, HttpStatus.FORBIDDEN),
    USER_NOT_FOUND("U000", "유저를 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_REGISTERED("U001", "이미 등록된 fingerprint 입니다. 관리자에게 문의해주세요.", HttpStatus.CONFLICT),
    USER_ALREADY_ENTERED_GAME_ROOM("U002", "이미 방에 입장한 상태입니다.", HttpStatus.CONFLICT),
    GAME_ROOM_NOT_FOUND("R000", "방을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    GAME_ROOM_JOIN_CODE_NOT_GENERATED("R001", "입장 코드 생성 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    GAME_ROOM_HAS_ILLEGAL_STATUS("R002", "방이 입장할 수 있는 상태가 아닙니다.", HttpStatus.CONFLICT),
    GAME_ROOM_ENTRANCE_NOT_FOUND("R003", "방에 입장한 기록을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    ;

    @Getter
    private final String code;
    @Getter
    private final String defaultMessage;
    @Getter
    private final HttpStatus httpStatus;

    /**
     * Security 관련 에러 코드의 메세지는 모호하게 처리
     */
    private static final String UNAUTHORIZED_MESSAGE = "인증에 실패했습니다.";
    private static final String ACCESS_DENIED_MESSAGE = "접근 권한이 없습니다.";
}
