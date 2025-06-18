package com.bb.webcanvasservice.game.application.repository;

import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipant;
import com.bb.webcanvasservice.game.domain.model.gameroom.GameRoomParticipantState;

import java.util.List;
import java.util.Optional;

/**
 * 게임 방 입장 정보를 담당하는 도메인 레이어 레포지토리
 */
public interface GameRoomEntranceRepository {

    /**
     * 게임 방 입장 객체를 ID로 조회한다.
     * @param gameRoomEntranceId 게임 방 입장 ID
     * @return 게임 방 입장 객체
     */
    Optional<GameRoomParticipant> findById(Long gameRoomEntranceId);

    /**
     * 게임 방 입장 객체를 저장한다.
     * @param gameRoomParticipant 저장할 게임 방 입장 객체
     * @return 저장된 게임 방 입장 객체
     */
    GameRoomParticipant save(GameRoomParticipant gameRoomParticipant);

    /**
     * gameRoom에 입장되어 있는지 여부를 조회한다.
     * @param gameRoomId 게임 방 ID
     * @param userId 유저 ID
     * @return 유효한 입장인지 여부
     */
    boolean existsActiveEntrance(Long gameRoomId, Long userId);

    /**
     * 게임 방에 현재 입장해있는 입장 유저 수를 조회한다.
     * @return 입장해 있는 유저 수
     */
    int findEnteredUserCount(Long gameRoomId);

    /**
     * 게임 방 입장 여부 조회
     * @param userId 조회 대상 유저 ID
     * @return 게임 방 입장 여부
     */
    boolean existsGameRoomEntranceByUserId(Long userId);

    /**
     * 입장한 방 찾기
     * @param userId 조회 대상 유저 ID
     * @return 게임 방 입장 정보
     */
    Optional<GameRoomParticipant> findByUserId(Long userId);

    /**
     * 게임 방 ID로 해당 게임 방에 입장한 정보 조회 (비관적 락 적용)
     *
     * 250430 - 입장한 순서대로 정렬 추가
     * @param gameRoomId 게임 방 ID
     * @return 게임 방 입장 정보
     */
    List<GameRoomParticipant> findGameRoomEntrancesByGameRoomIdWithLock(Long gameRoomId);

    /**
     * 유저 ID로 현재 입장한 게임 방의 입장 정보 조회
     * @param userId 조회 대상 유저 ID
     * @return 현재 입장한 게임 방의 입장 객체
     */
    Optional<GameRoomParticipant> findCurrentEnteredGameRoomEntranceByUserId(Long userId);

    /**
     * 게임 방 ID와 상태로 GameRoomEntrance 목록을 조회한다.
     * @param gameRoomId 게임 방 ID
     * @param gameRoomParticipantState 게임 방 입장 상태
     * @return 게임 방 입장 객체 리스트
     */
    List<GameRoomParticipant> findGameRoomEntrancesByGameRoomIdAndState(Long gameRoomId, GameRoomParticipantState gameRoomParticipantState);


    /**
     * 게임 방에 입장해있는 유저 수 조회
     * @param gameRoomId 게임 방 ID
     * @param gameRoomParticipantState 게임 방 입장 상태
     * @return 유저 수
     */
    long findGameRoomEntranceCountByGameRoomIdAndState(Long gameRoomId, GameRoomParticipantState gameRoomParticipantState);

    /**
     * 게임 방 ID와 상태들로 GameRoomEntrance 목록을 조회한다.
     * @param gameRoomId 게임 방 ID
     * @param gameRoomParticipantStates 조회 대상 게임 방 입장 상태 목록
     * @return 게임 방 입장 정보
     */
    List<GameRoomParticipant> findGameRoomEntrancesByGameRoomIdAndStates(Long gameRoomId, List<GameRoomParticipantState> gameRoomParticipantStates);

    /**
     * 게임 방 입장 상태를 일괄 업데이트한다.
     * @param gameRoomEntranceIds 게임 방 입장 ID 목록
     * @param state 변경하려는 상태 코드
     */
    void updateGameRoomEntrancesState(List<Long> gameRoomEntranceIds, GameRoomParticipantState state);

    /**
     * 게임 방 입장 정보 ID 목록에 해당하는 게임 방 입장 정보 조회
     * @param gameRoomEntranceIds 게임 방 입장 정보 ID 목록
     * @return 게임 방 입장 정보 목록
     */
    List<GameRoomParticipant> findGameRoomEntrancesByIds(List<Long> gameRoomEntranceIds);

    /**
     * 게임 방 입장 정보를 일괄 저장한다.
     * @param gameRoomParticipants 게임 방 입장 정보 목록
     */
    void saveAll(List<GameRoomParticipant> gameRoomParticipants);
}
