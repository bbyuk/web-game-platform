package com.bb.webcanvasservice.domain.game.service;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.domain.game.exception.*;
import com.bb.webcanvasservice.domain.game.model.*;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;

import java.util.List;

import static com.bb.webcanvasservice.domain.game.model.GameRoomEntranceState.WAITING;

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
        checkUserCanEnterGameRoom(userId);

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

    public void checkGameRoomCanEnter(Long gameRoomId, Long userId, GameRoomEntranceRole role, int gameRoomCapacity) {
        /**
         * 요청한 유저가 새로운 게임 방에 입장할 수 있는 상태인지 체크한다.
         */
        checkUserCanEnterGameRoom(userId);

        GameRoom targetGameRoom = gameRoomRepository.findById(gameRoomId).orElseThrow(GameRoomNotFoundException::new);
        if (!targetGameRoom.isWaiting()) {
            throw new IllegalGameRoomStateException();
        }

        List<GameRoomEntrance> targetGameRoomEntrances = gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdAndState(gameRoomId, WAITING);

        int enteredUserCounts = targetGameRoomEntrances.size();

        if (enteredUserCounts >= gameRoomCapacity) {
            throw new IllegalGameRoomStateException("방의 정원이 모두 찼습니다.");
        }
    }

    /**
     * 유저가 게임 방에 입장할 수 있는 상태인지 확인한다.
     * @param userId 대상 유저 ID
     */
    public void checkUserCanEnterGameRoom(Long userId) {
        if (gameRoomEntranceRepository.existsGameRoomEntranceByUserId(userId)) {
            throw new AlreadyEnteredRoomException();
        }
    }

    /**
     * 게임 방 ID와 게임 방 입장 상태에 맞는 게임 방 입장 목록을 조회해온다.
     *
     * @param gameRoomId 대상 게임 방 ID
     * @param gameRoomEntranceState 게임 방 입장 상태
     * @return 게임 방 입장 목록
     */
    public List<GameRoomEntrance> findGameRoomEntrancesByGameRoomIdAndState(Long gameRoomId, GameRoomEntranceState gameRoomEntranceState) {
        return gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdAndState(gameRoomId, gameRoomEntranceState);
    }

    /**
     * 파라미터로 전달 받은 state에 있는 GameRoom 목록을 조회한다.
     *
     * @param state 게임 방의 상태
     * @return gameRoomList 게임 방 Entity List
     */
    public List<GameRoom> findRoomsOnState(GameRoomState state) {
        return gameRoomRepository.findByState(state);
    }

    /**
     * 게임 방 입장 여부를 확인한다.
     *
     * @param gameRoomId 대상 게임 방 ID
     * @param userId 유저 ID
     * @return 게임 방 입장 여부
     */
    public boolean isEnteredRoom(Long gameRoomId, Long userId) {
        return gameRoomEntranceRepository.existsActiveEntrance(gameRoomId, userId);
    }

    /**
     * 게임 방을 찾는다.
     *
     * @param gameRoomId 게임 방 ID
     * @return 게임 방
     */
    public GameRoom findGameRoom(Long gameRoomId) {
        return gameRoomRepository.findById(gameRoomId)
                .orElseThrow(GameRoomNotFoundException::new);
    }

    public void validateIsHost(Long gameRoomId, Long userId) {
        GameRoomEntrance userEntrance = gameRoomEntranceRepository.findCurrentEnteredGameRoomEntranceByUserId(userId)
                .orElseThrow(GameRoomEntranceNotFoundException::new);

        if (!userEntrance.isHost()) {
            throw new AbnormalAccessException();
        }
    }
}

