package com.bb.webcanvasservice.domain.game;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임 domain 주입 설정 클래스
 * @param gameRoomCapacity
 * @param joinCodeMaxConflictCount
 * @param joinCodeLength
 */
@ConfigurationProperties(prefix = "application.domain.game")
public record GameProperties(

        // 게임 방 입장 최대 정원
        int gameRoomCapacity,

        // 게임 방 생성중 입장코드 충돌 발생 시 재생성 시도 최대 횟수
        int joinCodeMaxConflictCount,

        // 게임 방의 입장 코드 길이
        int joinCodeLength,

        // 게임 방에 입장한 유저들의 노출 컬러
        List<String> gameRoomUserColors,

        // 게임 방에 입장한 유저들의 랜덤 닉네임의 명사부분
        List<String> gameRoomUserNicknameNouns
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
        if (gameRoomUserColors == null) gameRoomUserColors = new ArrayList<>();
        if (gameRoomUserNicknameNouns == null) gameRoomUserNicknameNouns = new ArrayList<>();
    }
}
