package com.bb.webcanvasservice.domain.game.service;

import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceInfoResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomListResponse;
import com.bb.webcanvasservice.domain.game.entity.GameRoom;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게임 방과 관련된 서비스 Facade
 */
@Service
@RequiredArgsConstructor
public class GameRoomFacade {

    /**
     * 로비 서비스
     */
    private final LobbyService lobbyService;

    /**
     * 게임 방 내 서비스
     */
    private final InGameRoomService inGameRoomService;

    /**
     * 크로스 도메인 서비스
     */
    private final GameRoomCrossDomainService gameRoomCrossDomainService;





    /**
     * joinCode가 사용 가능한지 verify한다.
     * ACTIVE 상태 (WAITING || PLAYING)인 GameRoom들 중 파라미터로 전달 받은 joinCode가 충돌이 발생하는지 여부를
     * PESSIMISTIC_WRITE 락을 걸어 조회해 확인 후 충돌 발생시 재생성 해 verify 한다.
     *
     * @param joinCode
     * @return verifiedJoinCode
     */
    @Transactional
    public String verifyJoinCode(String joinCode) {
        return lobbyService.verifyJoinCode(joinCode);
    }

    /**
     * 게임 방을 새로 생성해 게임 방 ID를 리턴한다.
     *
     * @param userId
     * @return gameRoomId
     */
    @Transactional
    public Long createGameRoom(Long userId) {
        return lobbyService.createGameRoom(userId);
    }

    /**
     * 게임 방을 새로 생성하고, 생성을 요청한 유저를 입장시킨다.
     *
     * @param userId
     * @return gameRoomEntranceId
     */
    @Transactional
    public GameRoomEntranceResponse createGameRoomAndEnter(Long userId) {
        return lobbyService.createGameRoomAndEnter(userId);
    }

    /**
     * 게임 방에 유저를 입장시킨다.
     * <p>
     * - 입장시키려는 유저가 현재 아무 방에도 접속하지 않은 상태여야 한다.
     * - 입장하려는 방의 상태가 WAITING이어야 한다.
     * - 입장하려는 방에 접속한 유저 세션의 수(entrances)는 최대 8이다.
     *
     * @param gameRoomId
     * @param userId
     * @return gameRoomEntranceId
     */
    @Transactional
    public GameRoomEntranceResponse enterGameRoom(Long gameRoomId, Long userId, GameRoomRole role) {
        return lobbyService.enterGameRoom(gameRoomId, userId, role);
    }

    /**
     * 입장 가능한 게임 방을 조회해 리턴한다.
     * <p>
     * 이미 입장한 방이 있는 경우, AlreadyEnteredRoomException 을 throw한다.
     * <p>
     * TODO 메모리에서 WAITING GameRoomEntrance filtering 처리 -> batch size 및 페이징 처리 필요
     *
     * @param userId 유저 ID
     * @return GameRoomListResponse 게임 방 조회 응답 DTO
     */
    @Transactional
    public GameRoomListResponse findEnterableGameRooms(Long userId) {
        return lobbyService.findEnterableGameRooms(userId);
    }

    /**
     * Join Code로 게임 방에 입장한다.
     *
     * @param joinCode
     * @param userId
     * @return
     */
    @Transactional
    public GameRoomEntranceResponse enterGameRoomWithJoinCode(String joinCode, Long userId) {
        return lobbyService.enterGameRoomWithJoinCode(joinCode, userId);
    }

    /**
     * 현재 입장한 게임 방과 입장 정보를 리턴한다.
     * <p>
     * <p>
     * 250430 - 유저 Summary 데이터에 노출 컬러 필드 추가
     *
     * @param userId
     * @return
     */
    @Transactional
    public GameRoomEntranceInfoResponse findEnteredGameRoomInfo(Long userId) {
        return inGameRoomService.findEnteredGameRoomInfo(userId);
    }

    /**
     * 게임 방에서 퇴장한다.
     * <p>
     * HOST 퇴장 시 입장한 지 가장 오래된 유저가 HOST로 변경
     *
     * @param gameRoomEntranceId
     * @param userId
     */
    @Transactional
    public void exitFromRoom(Long gameRoomEntranceId, Long userId) {
        inGameRoomService.exitFromRoom(gameRoomEntranceId, userId);
    }

    /**
     * 게임 방에 입장한 유저의 레디 값을 변경한다.
     *
     * @param gameRoomEntranceId
     * @param userId
     * @param ready
     * @return
     */
    @Transactional
    public boolean updateReady(Long gameRoomEntranceId, Long userId, boolean ready) {
        return inGameRoomService.updateReady(gameRoomEntranceId, userId, ready);
    }

    /**
     * 게임 방 ID와 게임 방 입장 상태에 맞는 게임 방 입장 목록을 조회해온다.
     *
     * @param gameRoomId
     * @param gameRoomEntranceState
     * @return
     */
    @Transactional(readOnly = true)
    public List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdAndState(Long gameRoomId, GameRoomEntranceState gameRoomEntranceState) {
        return gameRoomCrossDomainService.findGameRoomEntrancesByGameRoomIdAndState(gameRoomId, gameRoomEntranceState);
    }

    /**
     * 파라미터로 전달 받은 state에 있는 GameRoom 목록을 조회한다.
     *
     * @param state 게임 방의 상태
     * @return gameRoomList 게임 방 Entity List
     */
    @Transactional(readOnly = true)
    public List<GameRoom> findRoomsOnState(GameRoomState state) {
        return gameRoomCrossDomainService.findRoomsOnState(state);
    }

    /**
     * 락을 걸어 현재 게임 방에 입장한 유저 입장 목록을 가져온다.
     *
     * @param gameRoomId
     * @return
     */
    @Transactional
    public List<GameRoomEntrance> findCurrentGameRoomEntrancesWithLock(Long gameRoomId) {
        return gameRoomCrossDomainService.findCurrentGameRoomEntrancesWithLock(gameRoomId);
    }

    /**
     * 게임 방 입장 여부를 확인한다.
     *
     * @param gameRoomId
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isEnteredRoom(Long gameRoomId, Long userId) {
        return gameRoomCrossDomainService.isEnteredRoom(gameRoomId, userId);
    }

    /**
     * 게임 방을 찾는다.
     *
     * @param gameRoomId
     * @return
     */
    @Transactional(readOnly = true)
    public GameRoom findGameRoom(Long gameRoomId) {
        return gameRoomCrossDomainService.findGameRoom(gameRoomId);
    }
}
