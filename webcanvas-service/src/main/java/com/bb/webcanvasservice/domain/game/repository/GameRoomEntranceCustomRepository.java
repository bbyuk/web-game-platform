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

}
