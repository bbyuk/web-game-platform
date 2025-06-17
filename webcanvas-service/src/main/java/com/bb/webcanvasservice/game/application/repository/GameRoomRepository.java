package com.bb.webcanvasservice.game.application.repository;

import com.bb.webcanvasservice.game.domain.model.GameRoom;
import com.bb.webcanvasservice.game.domain.model.GameRoomEntranceState;
import com.bb.webcanvasservice.game.domain.model.GameRoomState;

import java.util.List;
import java.util.Optional;

/**
 * 게임 방과 관련된 도메인 Repository
 */
public interface GameRoomRepository {

    /**
     * 게임 방 ID로 게임 방을 찾는다.
     * @param gameRoomId
     * @return 게임 방
     */
    Optional<GameRoom> findById(Long gameRoomId);

    /**
     * 현재 입장해있는 방을 조회한다.
     * @param userId 유저ID
     * @return gameRoom 현재 입장해있는 방
     */
    Optional<GameRoom> findNotClosedGameRoomByUserId(Long userId);

    /**
     * 현재 입장 가능한 방들 중 joinCode의 충돌이 있는지 여부를 비관적 락을 걸어 확인한다.
     * @param joinCode
     * @return
     */
    boolean existsJoinCodeConflictOnActiveGameRoom(String joinCode);

    /**
     * 입장 가능한 게임 방의 목록을 가져온다.
     *
     * 조건 1. GameRoom과 연관된 GameRoomEntrance의 수가 게임 방의 수용 인원 수보다 작거나 같아야함
     * 조건 2. GameRoom의 state가 enterableStates에 맞는 엔티티만 조회
     * @param gameRoomCapacity
     * @param enterableStates
     * @return
     */
    List<GameRoom> findGameRoomsByCapacityAndStateWithEntranceState(int gameRoomCapacity,
                                                                    List<GameRoomState> enterableStates,
                                                                    GameRoomEntranceState activeEntranceState);

    /**
     * GameRoom 상태로 게엠 방을 조회한다.
     * @param state
     * @return
     */
    List<GameRoom> findByState(GameRoomState state);

    /**
     * JoinCode로 입장할 방 조회
     * GameRoom.state = 'WAITING' 이어야 한다.
     *
     * @param joinCode
     * @return
     */
    Optional<GameRoom> findRoomWithJoinCodeForEnter(String joinCode);

    GameRoom save(GameRoom gameRoom);
}
