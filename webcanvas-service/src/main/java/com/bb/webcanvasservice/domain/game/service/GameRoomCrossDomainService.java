package com.bb.webcanvasservice.domain.game.service;

import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameRoomJpaEntity;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameRoomEntranceJpaEntity;
import com.bb.webcanvasservice.domain.game.model.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.model.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomEntranceJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Game 도메인 외에 다른 크로스 도메인에서 사용할 수 있도록 제공하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoomCrossDomainService {

    private final GameRoomEntranceJpaRepository gameRoomEntranceRepository;
    private final GameRoomJpaRepository gameRoomRepository;

    /**
     * 게임 방 ID와 게임 방 입장 상태에 맞는 게임 방 입장 목록을 조회해온다.
     *
     * @param gameRoomId
     * @param gameRoomEntranceState
     * @return
     */
    @Transactional(readOnly = true)
    public List<GameRoomEntranceJpaEntity> findGameRoomEntrancesByGameRoomIdAndState(Long gameRoomId, GameRoomEntranceState gameRoomEntranceState) {
        return gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdAndState(gameRoomId, gameRoomEntranceState);
    }

    /**
     * 파라미터로 전달 받은 state에 있는 GameRoom 목록을 조회한다.
     *
     * @param state 게임 방의 상태
     * @return gameRoomList 게임 방 Entity List
     */
    @Transactional(readOnly = true)
    public List<GameRoomJpaEntity> findRoomsOnState(GameRoomState state) {
        return gameRoomRepository.findByState(state);
    }

    /**
     * 락을 걸어 현재 게임 방에 입장한 유저 입장 목록을 가져온다.
     *
     * @param gameRoomId
     * @return
     */
    @Transactional
    public List<GameRoomEntranceJpaEntity> findCurrentGameRoomEntrancesWithLock(Long gameRoomId) {
        return gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdWithLock(gameRoomId);
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
        return gameRoomEntranceRepository.existsActiveEntrance(gameRoomId, userId);
    }

    /**
     * 게임 방을 찾는다.
     *
     * @param gameRoomId
     * @return
     */
    @Transactional(readOnly = true)
    public GameRoomJpaEntity findGameRoom(Long gameRoomId) {
        return gameRoomRepository.findById(gameRoomId)
                .orElseThrow(GameRoomNotFoundException::new);
    }
}
