package com.bb.webcanvasservice.domain.game.service;


import com.bb.webcanvasservice.common.util.JoinCodeGenerator;
import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import com.bb.webcanvasservice.domain.game.GameProperties;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceResponse;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomListResponse;
import com.bb.webcanvasservice.domain.game.entity.GameRoom;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomRole;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.event.GameRoomEntranceEvent;
import com.bb.webcanvasservice.domain.game.exception.AlreadyEnteredRoomException;
import com.bb.webcanvasservice.domain.game.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.domain.game.exception.JoinCodeNotGeneratedException;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.entity.User;
import com.bb.webcanvasservice.domain.user.enums.UserStateCode;
import com.bb.webcanvasservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState.WAITING;
import static com.bb.webcanvasservice.domain.game.enums.GameRoomRole.HOST;

@Slf4j
@Service
@RequiredArgsConstructor
public class LobbyService {

    private final GameProperties gameProperties;

    private final UserService userService;
    private final DictionaryService dictionaryService;

    private final GameRoomRepository gameRoomRepository;
    private final GameRoomEntranceRepository gameRoomEntranceRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

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
            if (conflictCount == gameProperties.joinCodeMaxConflictCount()) {
                log.error("join code 생성 중 충돌이 최대 횟수인 {}회 발생했습니다.", gameProperties.joinCodeMaxConflictCount());
                throw new JoinCodeNotGeneratedException();
            }
            conflictCount++;
            verifiedJoinCode = JoinCodeGenerator.generate(gameProperties.joinCodeLength());
        }
        return verifiedJoinCode;
    }


    /**
     * 게임 방을 새로 생성해 게임 방 ID를 리턴한다.
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
        User user = userService.findUser(userId);
        if (gameRoomEntranceRepository.existsGameRoomEntranceByUserId(user.getId())) {
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
        String joinCode = verifyJoinCode(JoinCodeGenerator.generate(gameProperties.joinCodeLength()));


        /**
         * GameRoom 생성
         */
        GameRoom newGameRoom = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, joinCode));

        return newGameRoom.getId();
    }

    /**
     * 게임 방을 새로 생성하고, 생성을 요청한 유저를 입장시킨다.
     *
     * @param userId
     * @return gameRoomEntranceId
     */
    @Transactional
    public GameRoomEntranceResponse createGameRoomAndEnter(Long userId) {
        Long gameRoomId = createGameRoom(userId);
        return enterGameRoom(gameRoomId, userId, HOST);
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
        if (gameRoomEntranceRepository.existsGameRoomEntranceByUserId(userId)) {
            throw new AlreadyEnteredRoomException();
        }

        GameRoom targetGameRoom = gameRoomRepository.findById(gameRoomId).orElseThrow(GameRoomNotFoundException::new);
        if (!targetGameRoom.getState().equals(GameRoomState.WAITING)) {
            throw new IllegalGameRoomStateException();
        }

        List<GameRoomEntrance> gameRoomEntrances = gameRoomEntranceRepository.findGameRoomEntrancesByGameRoomIdAndState(gameRoomId, WAITING);
        int enteredUserCounts = gameRoomEntrances.size();

        if (enteredUserCounts >= gameProperties.gameRoomCapacity()) {
            throw new IllegalGameRoomStateException("방의 정원이 모두 찼습니다.");
        }

        String koreanAdjective = dictionaryService.drawRandomWordValue(Language.KOREAN, PartOfSpeech.ADJECTIVE);
        GameRoomEntrance gameRoomEntrance =
                new GameRoomEntrance(
                        targetGameRoom
                        , userService.findUser(userId)
                        , String.format("%s %s", koreanAdjective, gameProperties.gameRoomUserNicknameNouns().get(enteredUserCounts))
                        , role
                );

        GameRoomEntrance newGameRoomEntrance = gameRoomEntranceRepository.save(gameRoomEntrance);

        userService.changeUserState(userId, UserStateCode.IN_ROOM);


        /**
         * 게임 방 입장 이벤트 pub ->
         * 게임 방 broker에 입장 send 위임
         */
        applicationEventPublisher.publishEvent(new GameRoomEntranceEvent(gameRoomId, userId));

        return new GameRoomEntranceResponse(targetGameRoom.getId(), newGameRoomEntrance.getId());
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
    @Transactional(readOnly = true)
    public GameRoomListResponse findEnterableGameRooms(Long userId) {
        if (gameRoomEntranceRepository.existsGameRoomEntranceByUserId(userId)) {
            throw new AlreadyEnteredRoomException("이미 방에 입장한 상태입니다.");
        }



        return new GameRoomListResponse(
                gameRoomRepository.findGameRoomsByCapacityAndStateWithEntranceState(gameProperties.gameRoomCapacity(), GameRoomState.enterable(), WAITING)
                        .stream()
                        .map(gameRoom ->
                                new GameRoomListResponse.GameRoomSummary(
                                        gameRoom.getId(),
                                        gameProperties.gameRoomCapacity(),
                                        (int) gameRoomEntranceRepository.findGameRoomEntranceCountByGameRoomIdAndState(gameRoom.getId(), WAITING),
                                        gameRoom.getJoinCode()
                                )
                        )
                        .collect(Collectors.toList())
        );
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
        GameRoom targetGameRoom = gameRoomRepository.findRoomWithJoinCodeForEnter(joinCode)
                .orElseThrow(() -> new GameRoomNotFoundException(String.format("입장 코드가 %s인 방을 찾지 못했습니다.", joinCode)));

        return enterGameRoom(targetGameRoom.getId(), userId, GameRoomRole.GUEST);
    }
}
