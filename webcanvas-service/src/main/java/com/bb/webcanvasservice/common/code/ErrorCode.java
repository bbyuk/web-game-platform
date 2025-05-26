package com.bb.webcanvasservice.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * API 처리 중 발생한 예외에 대해 클라이언트로 내려줄 에러 코드
 *
 * === code 필드는 아래 정책에 따라 prefix를 갖는다.
 * A - 인증 관련 에러
 * B - 밴 유발 기록 로깅
 * U - 유저 관련 에러
 * R - 게임 방 및 게임 방 입장 관련 에러
 * G - 게임 관련 에러
 * D - 사전 관련 에러
 * S - 시스템 관련 에러
 * C - 클라이언트 요청 오류
 */
@RequiredArgsConstructor
public enum ErrorCode {
    SYSTEM_ERROR("S000", "시스템 오류입니다. 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("C000", "잘못된 접근입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("A000", "인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("A000", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("A001", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    MISSING_AUTH_HEADER("A002", "Authorization 헤더가 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    MALFORMED_TOKEN("A003", "토큰 형식이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("A004", "지원하지 않는 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_SIGNATURE("A005", "토큰 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_YET_VALID("A006", "토큰 사용 가능 시간이 아직 되지 않았습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_ISSUED_AT_ERROR("A007", "토큰 발급 시간에 문제가 있습니다.", HttpStatus.UNAUTHORIZED),
    CLAIM_VALIDATION_FAILED("A008", "토큰 클레임 유효성 검증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    BLACKLISTED_TOKEN("A009", "사용이 차단된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    CORS_REJECTED("A010", "CORS 정책으로 인해 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    AUTH_HEADER_FORMAT_INVALID("A011", "Authorization 헤더 형식이 잘못되었습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_PARSING_ERROR("A012", "토큰 파싱 도중 오류가 발생했습니다.", HttpStatus.UNAUTHORIZED),
    AUTH_USER_NOT_FOUND("A013", "인증 대상 사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND("A014", "Refresh Token을 찾지 못했습니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("A999", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND("U000", "유저를 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_REGISTERED("U001", "이미 등록된 fingerprint 입니다. 관리자에게 문의해주세요.", HttpStatus.CONFLICT),
    USER_ALREADY_ENTERED_GAME_ROOM("U002", "이미 방에 입장한 상태입니다.", HttpStatus.CONFLICT),
    GAME_ROOM_NOT_FOUND("R000", "방을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    GAME_ROOM_JOIN_CODE_NOT_GENERATED("R001", "입장 코드 생성 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    GAME_ROOM_HAS_ILLEGAL_STATUS("R002", "방이 입장할 수 있는 상태가 아닙니다.", HttpStatus.CONFLICT),
    GAME_ROOM_ENTRANCE_NOT_FOUND("R003", "방에 입장한 기록을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    DICTIONARY_FILE_PARSE_FAILED("D000", "사전 파일을 파싱하는 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DICTIONARY_FILE_DOWNLOAD_FAILED("D001", "사전 파일을 다운로드 받는 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    WORD_NOT_FOUND("D002", "단어를 찾지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    GAME_SESSION_NOT_FOUND("G000", "진행중인 게임을 찾지 못했습니다.", HttpStatus.NOT_FOUND),
    GAME_SESSION_IS_OVER("G001", "게임 세션이 종료되었습니다.", HttpStatus.CONFLICT),
    ABNORMAL_ACCESS("B000", "비정상적인 접근이 감지되었습니다.", HttpStatus.BAD_REQUEST);

    @Getter
    private final String code;
    @Getter
    private final String defaultMessage;
    @Getter
    private final HttpStatus httpStatus;
}
