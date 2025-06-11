package com.bb.webcanvasservice.domain.game.repository;

import com.bb.webcanvasservice.domain.game.model.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceState;

import java.util.List;
import java.util.Optional;

/**
 * 게임 방 입장 정보를 담당하는 도메인 레이어 레포지토리
 */
public interface GameRoomEntranceRepository {
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

    /**
     * 게임 방 입장 여부 조회
     * @param userId
     * @return 게임 방 입장 여부
     */
    boolean existsGameRoomEntranceByUserId(Long userId);

    /**
     * 입장한 방 찾기
     * @param userId 유저 ID
     * @return 게임 방 입장 정보
     */
    Optional<GameRoomEntrance> findByUserId(Long userId);

    /**
     * 게임 방 ID로 해당 게임 방에 입장한 정보 조회 (비관적 락 적용)
     *
     * 250430 - 입장한 순서대로 정렬 추가
     * @param gameRoomId 게임 방 ID
     * @return 게임 방 입장 정보
     */
    List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdWithLock(Long gameRoomId);

    /**
     * 유저 ID로 현재 입장한 게임 방의 입장 정보 조회
     * @param userId
     * @return
     */
    Optional<GameRoomEntrance> findGameRoomEntranceByUserId(Long userId, List<GameRoomEntranceState> gameRoomEntranceStates);

    /**
     * 게임 방 ID와 상태로 GameRoomEntrance 목록을 조회한다.
     * @param gameRoomId
     * @param gameRoomEntranceState
     * @return
     */
    List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdAndState(Long gameRoomId, GameRoomEntranceState gameRoomEntranceState);


    /**
     * 게임 방에 입장해있는 유저 수 조회
     * @param gameRoomId 게임 방 ID
     * @param gameRoomEntranceState 게임 방 입장 상태
     * @return 유저 수
     */
    long findGameRoomEntranceCountByGameRoomIdAndState(Long gameRoomId, GameRoomEntranceState gameRoomEntranceState);

    /**
     * 게임 방 ID와 상태들로 GameRoomEntrance 목록을 조회한다.
     * @param gameRoomId 게임 방 ID
     * @param gameRoomEntranceStates 조회 대상 게임 방 입장 상태 목록
     * @return 게임 방 입장 정보
     */
    List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdAndStates(Long gameRoomId, List<GameRoomEntranceState> gameRoomEntranceStates);
}
