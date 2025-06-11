package com.bb.webcanvasservice.domain.game.service;

import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.domain.game.exception.AlreadyEnteredRoomException;
import com.bb.webcanvasservice.domain.game.exception.JoinCodeNotGeneratedException;
import com.bb.webcanvasservice.domain.game.model.GameRoom;
import com.bb.webcanvasservice.domain.game.model.GameRoomState;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.entity.GameRoomJpaEntity;

/**
 * 게임 방 관련 도메인 서비스
 */
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final GameRoomEntranceRepository gameRoomEntranceRepository;

    public GameRoomService(GameRoomRepository gameRoomRepository,
                           GameRoomEntranceRepository gameRoomEntranceRepository) {
        this.gameRoomRepository = gameRoomRepository;
        this.gameRoomEntranceRepository = gameRoomEntranceRepository;
    }

    public void checkCanEnterTheRoom(Long userId) {
        /**
         * 유저가 새로 게임을 생성할 수 있는 상태인지 확인한다.
         * - 유저가 현재 아무 방에도 입장하지 않은 상태여야 한다.
         */
        if (gameRoomEntranceRepository.existsGameRoomEntranceByUserId(userId)) {
            throw new AlreadyEnteredRoomException();
        }
    }

    /**
     * 게임 방 입장코드를 사용할 수 있는지 verify 하고 충돌 발생시 최대 충돌 가능 threshold까지 redraw하여 리턴한다.
     * @param joinCode 대상 입장코드
     * @param joinCodeMaxConflictCount 최대 충돌 가능 threshold
     * @return verified join code
     */
    public String verifyJoinCode(String joinCode, int joinCodeMaxConflictCount) {
        String verifiedJoinCode = joinCode;
        int conflictCount = 0;

        while (gameRoomRepository.existsJoinCodeConflictOnActiveGameRoom(verifiedJoinCode)) {
            if (conflictCount == joinCodeMaxConflictCount) {
                throw new JoinCodeNotGeneratedException();
            }
            conflictCount++;
            verifiedJoinCode = JoinCodeGenerator.generate(joinCode.length());
        }
        return verifiedJoinCode;
    }

    public GameRoom createGameRoom(Long userId, int joinCodeLength, int joinCodeMaxConflictCount) {
        /**
         * 유저가 새로 게임을 생성할 수 있는 상태인지 확인한다.
         * - 유저가 현재 아무 방에도 입장하지 않은 상태여야 한다.
         */
        if (gameRoomEntranceRepository.existsGameRoomEntranceByUserId(userId)) {
            throw new AlreadyEnteredRoomException();
        }

        /**
         * join code 생성
         *
         * 1. 랜덤 코드 생성
         * 2. 랜덤 코드 충돌 확인
         *      2.1. 현재 방의 상태가 closed가 아닌 게임 방 중 생성된 랜덤코드와 동일한 joinCode를 가진 게임 방이 있는지 조회
         *      2.2. 충돌 시 1번 로직으로 이동
         *      2.3. 통과시 해당 랜덤코드를 새로 생성할 방의 joinCode로 채택
         *
         * conflict가 10번 초과하여 발생할 시 joinCode 생성 중 문제가 발생했다는 문구와 함께 잠시후 재시도 해달라는 문구 출력 필요
         */
        String joinCode = verifyJoinCode(JoinCodeGenerator.generate(joinCodeLength), joinCodeMaxConflictCount);


        /**
         * GameRoom 생성
         */
        return gameRoomRepository.save(new GameRoom(null ,joinCode, GameRoomState.WAITING));
    }

}
