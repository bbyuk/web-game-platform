package com.bb.webcanvasservice.domain.game;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 게임 domain 주입 설정 클래스
 * @param gameRoomCapacity
 * @param joinCodeMaxConflictCount
 * @param joinCodeLength
 */
@ConfigurationProperties(prefix = "application.domain.game")
public record GameProperties(
        /**
         * 게임 방 입장 최대 정원
         */
        int gameRoomCapacity,
        /**
         * 게임 방 생성중 입장코드 충돌 발생 시 재생성 시도 최대 횟수
         */
        int joinCodeMaxConflictCount,
        /**
         * 게임 방의 입장 코드 길이
         */
        int joinCodeLength
) {
    /**
     * default value setting을 위한 기본 생성자
     * @param gameRoomCapacity
     * @param joinCodeMaxConflictCount
     * @param joinCodeLength
     */
    public GameProperties {
        if (gameRoomCapacity == 0) gameRoomCapacity = 8;
        if (joinCodeMaxConflictCount == 0) joinCodeMaxConflictCount = 10;
        if (joinCodeLength == 0) joinCodeLength = 10;
    }
}
