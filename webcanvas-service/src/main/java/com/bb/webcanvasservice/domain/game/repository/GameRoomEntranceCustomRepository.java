package com.bb.webcanvasservice.domain.game.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * GameRoomEntrance 관련 커스텀 레포지토리
 */
public interface GameRoomEntranceCustomRepository {

    /**
     * gameRoom에 입장되어 있는지 여부를 조회한다.
     * @param gameRoomId
     * @param userId
     * @return
     */
    boolean existsActiveEntrance(Long gameRoomId, Long userId);

    /**
     * 게임 방에 현재 입장해있는 입장 유저 수를 조회한다.
     * @return 입장해 있는 유저 수
     */
    int findEnteredUserCount(Long gameRoomId);

}
