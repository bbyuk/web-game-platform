package com.bb.webcanvasservice.game.application.repository;

import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoom;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomState;

import java.util.List;
import java.util.Optional;

/**
 * 게임 방과 관련된 도메인 Repository
 */
public interface GameRoomRepository {

    /**
     * 게임 방 ID로 게임 방을 찾는다.
     * @param gameRoomId 게임 방 ID
     * @return 게임 방
     */
    Optional<GameRoom> findGameRoomById(Long gameRoomId);

    /**
     * 게임 입장자 ID로 대상 게임 방을 조회한다.
     * @param gameRoomParticipantId 게임 입장자 ID
     * @return 대상 게임 방 객체
     */
    Optional<GameRoom> findGameRoomByGameRoomParticipantId(Long gameRoomParticipantId);


    /**
     * 게임 세션 ID로 해당 게임 세션 ID가 진행되는 게임 방 객체를 조회한다.
     * @param gameSessionId 게임 세션 ID
     * @return 게임 방
     */
    Optional<GameRoom> findGameRoomByGameSessionId(Long gameSessionId);

    /**
     * 유저 ID로 대상 유저가 입장해 있는 게임 방을 찾는다.
     * @param userId 유저 ID
     * @return 게임 방
     */
    Optional<GameRoom> findCurrentJoinedGameRoomByUserId(Long userId);


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
     * @param gameRoomCapacity 게임 방 정원
     * @param gameRoomState 게임 방 상태
     * @param gameRoomParticipantState 게임 방 입장자 상태
     * @return
     */
    List<GameRoom> findGameRoomsByCapacityAndGameRoomStateAndGameRoomParticipantState(int gameRoomCapacity,
                                                                                      GameRoomState gameRoomState,
                                                                                      GameRoomParticipantState gameRoomParticipantState);

    /**
     * JoinCode로 입장할 방 조회
     * GameRoom.state = 'WAITING' 이어야 한다.
     *
     * @param joinCode
     * @return
     */
    Optional<GameRoom> findGameRoomByJoinCodeAndState(String joinCode, GameRoomState state);

    /**
     * 게임 방 애그리거트 루트를 저장한다.
     * @param gameRoom 게임 방 객체
     * @return 저장된 게임 방
     */
    GameRoom save(GameRoom gameRoom);


}
