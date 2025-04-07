package com.bb.webcanvasservice.domain.game;

import com.bb.webcanvasservice.common.RandomCodeGenerator;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomCreateResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomListResponse;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.exception.*;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게임 방과 관련된 비즈니스 로직을 처리하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoomService {

    /**
     * 서비스
     */
    private final UserService userService;

    /**
     * 레포지토리
     */
    private final GameRoomRepository gameRoomRepository;
    private final GameRoomEntranceRepository gameRoomEntranceRepository;

    /**
     * 요청자가 입장한 게임 방을 리턴한다.
     * 현재 입장한 게임 방이 없을 경우, GameRoomNotFoundException 발생
     *
     * @param userId
     * @return GameRoom
     */
    @Transactional(readOnly = true)
    public GameRoom findEnteredGameRoom(Long userId) {
        return gameRoomRepository.findNotClosedGameRoomByUserId(userId)
                .orElseThrow(() -> new GameRoomNotFoundException("현재 입장한 방을 찾읈 수 없습니다."));
    }

    /**
     * 게임 방을 새로 생성하고, 생성을 요청한 유저를 입장시킨다.
     *
     * @param userId
     * @return gameRoomEntranceId
     */
    @Transactional
    public GameRoomCreateResponse createGameRoomAndEnter(Long userId) {
        Long gameRoomId = createGameRoom(userId);
        Long gameRoomEntranceId = enterGameRoom(gameRoomId, userId);

        return new GameRoomCreateResponse(gameRoomId, gameRoomEntranceId);
    }

    /**
     * 게임 방을 새로 생성해 게임 방을 리턴한다.
     *
     * @param userId
     * @return gameRoomId
     */
    @Transactional
    public Long createGameRoom(Long userId) {
        /**
         * 유저가 새로 게임을 생성할 수 있는 상태인지 확인한다.
         * - 유저가 현재 아무 방에도 입장하지 않은 상태여야 한다.
         */
        User user = userService.findUserByUserId(userId);
        if (gameRoomEntranceRepository.existsGameRoomEntranceByUserId(user.getId())) {
            throw new AlreadyEnteredRoomException("이미 입장한 방이 있습니다.");
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
        String joinCode = verifyJoinCode(RandomCodeGenerator.generate(GameRoom.JOIN_CODE_LENGTH));


        /**
         * GameRoom 생성
         */
        GameRoom newGameRoom = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, joinCode));

        return newGameRoom.getId();
    }

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
        String verifiedJoinCode = joinCode;
        int conflictCount = 0;

        while (gameRoomRepository.existsJoinCodeConflictOnActiveGameRoom(verifiedJoinCode)) {
            if (conflictCount == GameRoom.JOIN_CODE_MAX_CONFLICT_COUNT) {
                log.error("join code 생성 중 충돌이 최대 횟수인 {}회 발생했습니다.", GameRoom.JOIN_CODE_MAX_CONFLICT_COUNT);
                throw new JoinCodeNotGeneratedException("join code 생성 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
            }
            conflictCount++;
            verifiedJoinCode = RandomCodeGenerator.generate(GameRoom.JOIN_CODE_LENGTH);
        }
        return verifiedJoinCode;
    }

    /**
     * 게임 방에 유저를 입장시킨다.
     * <p>
     * - 입장시키려는 유저가 현재 아무 방에도 접속하지 않은 상태여야 한다. -> 방에서 나갈 시 삭제 처리
     * - 입장하려는 방의 상태가 WAITING이어야 한다.
     * - 입장하려는 방에 접속한 유저 세션의 수(entrances)는 최대 8이다.
     *
     * @param gameRoomId
     * @param userId
     * @return gameRoomEntranceId
     */
    @Transactional
    public Long enterGameRoom(Long gameRoomId, Long userId) {
        if (gameRoomEntranceRepository.existsGameRoomEntranceByUserId(userId)) {
            throw new AlreadyEnteredRoomException("이미 입장한 방이 있습니다.");
        }

        GameRoom targetGameRoom = gameRoomRepository.findById(gameRoomId).orElseThrow(() -> new GameRoomNotFoundException("게임 방을 찾을 수 없습니다."));
        if (!targetGameRoom.getState().equals(GameRoomState.WAITING)) {
            throw new IllegalGameRoomStateException("방이 현재 입장할 수 없는 상태입니다.");
        }

        if (targetGameRoom.getEntrances().size() >= GameRoom.CAPACITY) {
            throw new IllegalGameRoomStateException("방의 정원이 모두 찼습니다.");
        }

        GameRoomEntrance gameRoomEntrance =
                new GameRoomEntrance(
                        targetGameRoom
                        , userService.findUserByUserId(userId));
        GameRoomEntrance newGameRoomEntrance = gameRoomEntranceRepository.save(gameRoomEntrance);
        targetGameRoom.addEntrance(newGameRoomEntrance);

        return newGameRoomEntrance.getId();
    }

    /**
     * 파라미터로 전달 받은 state에 있는 GameRoom 목록을 조회한다.
     *
     * @param state 게임 방의 상태
     * @return gameRoomList 게임 방 Entity List
     */
    @Transactional(readOnly = true)
    public List<GameRoom> findRoomsOnState(GameRoomState state) {
        return gameRoomRepository.findByState(state);
    }

    /**
     * 입장 가능한 게임 방을 조회해 리턴한다.
     * <p>
     * 이미 입장한 방이 있는 경우, AlreadyEnteredRoomException 을 throw한다.
     *
     * @param userId 유저 ID
     * @return GameRoomListResponse 게임 방 조회 응답 DTO
     */
    @Transactional(readOnly = true)
    public GameRoomListResponse findEnterableGameRooms(Long userId) {
        if (gameRoomEntranceRepository.existsGameRoomEntranceByUserId(userId)) {
            throw new AlreadyEnteredRoomException("이미 방에 입장한 상태입니다.");
        }


        return new GameRoomListResponse(
                gameRoomRepository.findEnterableGameRooms(GameRoom.CAPACITY, GameRoomState.enterable())
                        .stream()
                        .map(gameRoom -> new GameRoomListResponse.GameRoomSummary(gameRoom.getId(), gameRoom.getJoinCode()))
                        .collect(Collectors.toList())
        );
    }

    /**
     * 현재 입장한 게임 방과 입장 정보를 리턴한다.
     * <p>
     * TODO 개발 진행중 - 테스트코드 작성 필요
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public GameRoomEntranceResponse findEnteredGameRoomInfo(Long userId) {
        List<GameRoomEntrance> gameRoomEntrances = gameRoomEntranceRepository.findGameRoomEntrancesByUserId(userId);

        GameRoomEntrance userEntrance = gameRoomEntrances.stream()
                .filter(gameRoomEntrance -> gameRoomEntrance.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new GameRoomEntranceNotFoundException("현재 입장한 게임 방을 찾지 못했습니다."));

        return new GameRoomEntranceResponse(
                userEntrance.getGameRoom().getId(),
                userEntrance.getId(),
                gameRoomEntrances.stream().map(
                                entranceOfGameRoom ->
                                        new GameRoomEntranceResponse.EnteredUserSummary(
                                                entranceOfGameRoom.getUser().getId()
                                        )
                        )
                        .collect(Collectors.toList())
        );
    }
}
