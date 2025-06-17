package com.bb.webcanvasservice.game.application.port.user;

import java.util.List;

/**
 * game -> user command 포트
 */
public interface UserCommandPort {

    /**
     * 유저 ID 리스트로 대상 유저들의 상태를 GameSession 내부로 변경한다.
     * @param userIds 대상 유저 ID 리스트
     */
    void moveUsersToGameSession(List<Long> userIds);

    /**
     * 유저 ID 리스트로 대상 유저들의 상태를 GameRoom 내부로 변경한다.
     * @param userIds 대상 유저 ID 리스트
     */
    void moveUsersToRoom(List<Long> userIds);

    /**
     * 대상 유저의 상태를 GameRoom 내부로 변경한다.
     * @param userId 대상 유저 ID
     */
    void moveUserToRoom(Long userId);

    /**
     * 대상 유저의 상태를 로비로 변경한다.
     * @param userId 대상 유저 ID
     */
    void moveUserToLobby(Long userId);
}
