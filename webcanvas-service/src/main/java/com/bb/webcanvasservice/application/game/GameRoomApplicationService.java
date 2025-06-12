package com.bb.webcanvasservice.application.game;

import com.bb.webcanvasservice.application.game.command.EnterGameRoomCommand;
import com.bb.webcanvasservice.application.game.dto.*;
import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import com.bb.webcanvasservice.application.GameProperties;
import com.bb.webcanvasservice.domain.game.event.GameRoomEntranceEvent;
import com.bb.webcanvasservice.domain.game.event.GameRoomExitEvent;
import com.bb.webcanvasservice.domain.game.event.GameRoomHostChangedEvent;
import com.bb.webcanvasservice.domain.game.event.UserReadyChanged;
import com.bb.webcanvasservice.domain.game.exception.GameRoomEntranceNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.GameRoomHostCanNotChangeReadyException;
import com.bb.webcanvasservice.domain.game.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.domain.game.model.*;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.game.service.GameRoomService;
import com.bb.webcanvasservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.bb.webcanvasservice.domain.game.model.GameRoomEntranceRole.HOST;
import static com.bb.webcanvasservice.domain.game.model.GameRoomEntranceState.WAITING;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoomApplicationService {

    /**
     * 도메인 서비스
     */
    private final GameRoomService gameRoomService;


    /**
     * 크로스 도메인 서비스
     */
    private final DictionaryService dictionaryService;
    private final UserService userService;

    /**
     * 도메인 레포지토리
     */
    private final GameRoomRepository gameRoomRepository;
    private final GameRoomEntranceRepository gameRoomEntranceRepository;

    /**
     * common layer
     */
    private final GameProperties gameProperties;
    private final ApplicationEventPublisher eventPublisher;


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
        return gameRoomService.verifyJoinCode(joinCode, gameProperties.joinCodeMaxConflictCount());
    }

    /**
     * 게임 방을 새로 생성해 게임 방 ID를 리턴한다.
     *
     * @param userId
     * @return gameRoomId
     */
    @Transactional
    public GameRoom createGameRoom(Long userId) {
        return gameRoomService.createGameRoom(userId, gameProperties.joinCodeLength(), gameProperties.joinCodeMaxConflictCount());
    }

    /**
     * 게임 방을 새로 생성하고, 생성을 요청한 유저를 입장시킨다.
     *
     * @param userId
     * @return gameRoomEntranceId
     */
    @Transactional
    public GameRoomEntranceDto createGameRoomAndEnter(Long userId) {
        GameRoom gameRoom = createGameRoom(userId);
        return enterGameRoom(new EnterGameRoomCommand(gameRoom.getId(), userId, HOST));
    }

    /**
     * 게임 방에 유저를 입장시킨다.
     * <p>
     * - 입장시키려는 유저가 현재 아무 방에도 접속하지 않은 상태여야 한다.
     * - 입장하려는 방의 상태가 WAITING이어야 한다.
     * - 입장하려는 방에 접속한 유저 세션의 수(entrances)는 최대 8이다.
     *
     * @param command 게임 방 입장 커맨드
     * @return gameRoomEntranceId 게임 방 입장 ID
     */
    @Transactional
    public GameRoomEntranceDto enterGameRoom(EnterGameRoomCommand command) {
        /**
         * 대상 게임 방에 입장할 수 있는지 체크한다.
         */
        gameRoomService.checkGameRoomCanEnter(command.gameRoomId(), command.userId(), command.role(), gameProperties.gameRoomCapacity());

        /**
         * dictionary 도메인 서비스로부터 랜덤 형용사 조회
         * 명사와 결합하여 랜덤 닉네임 할당
         *
         * HOST로 입장한다면 entrance의 ready상태를 true로 초기 설정
         */
        String koreanAdjective = dictionaryService.drawRandomWordValue(Language.KOREAN, PartOfSpeech.ADJECTIVE);

        GameRoomEntrance newGameRoomEntrance = gameRoomEntranceRepository.save(
                new GameRoomEntrance(
                        null
                        , command.gameRoomId()
                        , command.userId()
                        , GameRoomEntranceState.WAITING
                        , String.format("%s %s", koreanAdjective, "플레이어")
                        , command.role()
                        , command.role() == GameRoomEntranceRole.HOST
                )
        );

        userService.moveUserToRoom(command.userId());

        /**
         * 게임 방 입장 이벤트 pub ->
         * 게임 방 broker에 입장 send 위임
         */
        eventPublisher.publishEvent(new GameRoomEntranceEvent(command.gameRoomId(), command.userId()));

        return new GameRoomEntranceDto(newGameRoomEntrance.getGameRoomId(), newGameRoomEntrance.getId());
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
    public GameRoomListDto findEnterableGameRooms(Long userId) {
        gameRoomService.checkUserCanEnterGameRoom(userId);

        return new GameRoomListDto(
                gameRoomRepository.findGameRoomsByCapacityAndStateWithEntranceState(gameProperties.gameRoomCapacity(), GameRoomState.enterable(), WAITING)
                        .stream()
                        .map(gameRoom ->
                                new GameRoomInfoDto(
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
    public GameRoomEntranceDto enterGameRoomWithJoinCode(String joinCode, Long userId) {
        GameRoom targetGameRoom = gameRoomRepository.findRoomWithJoinCodeForEnter(joinCode)
                .orElseThrow(() -> new GameRoomNotFoundException(String.format("입장 코드가 %s인 방을 찾지 못했습니다.", joinCode)));

        return enterGameRoom(
                new EnterGameRoomCommand(targetGameRoom.getId(), userId, GameRoomEntranceRole.GUEST)
        );
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
    @Transactional(readOnly = true)
    public GameRoomEntranceDetailInfoDto findEnteredGameRoomInfo(Long userId) {
        GameRoomEntrance userEntrance = gameRoomEntranceRepository.findCurrentEnteredGameRoomEntranceByUserId(userId)
                .orElseThrow(GameRoomEntranceNotFoundException::new);

        AtomicInteger index = new AtomicInteger(0);


        List<GameRoomEntrance> gameRoomEntrances = gameRoomEntranceRepository
                .findGameRoomEntrancesByGameRoomIdAndStates(userEntrance.getGameRoomId(), GameRoomEntranceState.entered);

        List<EnteredUserInfoDto> enteredUserSummaries = gameRoomEntrances
                .stream()
                .map(gameRoomEntrance ->
                        new EnteredUserInfoDto(
                                gameRoomEntrance.getUserId(),
                                gameProperties.gameRoomUserColors().get(index.getAndIncrement()),
                                gameRoomEntrance.getNickname(),
                                userEntrance.getRole(),
                                gameRoomEntrance.isReady()
                        )
                )
                .collect(Collectors.toList());

        GameRoom gameRoom = gameRoomRepository.findById(userEntrance.getGameRoomId()).orElseThrow(GameRoomNotFoundException::new);

        return new GameRoomEntranceDetailInfoDto(
                userEntrance.getGameRoomId(),
                userEntrance.getId(),
                enteredUserSummaries,
                gameRoom.getState(),
                enteredUserSummaries.stream().filter(enteredUserSummary
                        -> enteredUserSummary.userId().equals(userId)).findFirst().orElseThrow(GameRoomEntranceNotFoundException::new)
        );
    }

    /**
     * 게임 방에서 퇴장한다.
     * <p>
     * HOST 퇴장 시 입장한 지 가장 오래된 유저가 HOST로 변경
     *
     * @param gameRoomEntranceId 대상 게임 방 입장 ID
     * @param userId             유저 ID
     */
    @Transactional
    public void exitFromRoom(Long gameRoomEntranceId, Long userId) {
        GameRoomEntrance targetEntrance = gameRoomEntranceRepository.findById(gameRoomEntranceId)
                .orElseThrow(GameRoomEntranceNotFoundException::new);

        if (!targetEntrance.getUserId().equals(userId)) {
            log.error("대상 게임 방 입장 기록과 요청 유저가 다릅니다.");
            throw new AbnormalAccessException();
        }
        targetEntrance.exit();
        gameRoomEntranceRepository.save(targetEntrance);

        GameRoom gameRoom = gameRoomRepository.findById(targetEntrance.getGameRoomId()).orElseThrow(GameRoomNotFoundException::new);

        List<GameRoomEntrance> entrances = gameRoomEntranceRepository
                .findGameRoomEntrancesByGameRoomIdAndState(gameRoom.getId(), GameRoomEntranceState.WAITING);

        if (entrances.isEmpty()) {
            gameRoom.close();
            gameRoomRepository.save(gameRoom);
        } else if (targetEntrance.getRole() == HOST) {
            /**
             * 250522
             * 퇴장 요청을 보낸 유저가 HOST일 경우 남은 유저 중 제일 처음 입장한 유저가 HOST가 된다.
             */
            entrances.stream()
                    .filter(entrance -> !entrance.getId().equals(gameRoomEntranceId))
                    .findFirst()
                    .ifPresentOrElse(
                            entrance -> {
                                entrance.changeRole(HOST);
                                gameRoomEntranceRepository.save(entrance);
                                // HOST changed event 발행

                                eventPublisher.publishEvent(new GameRoomHostChangedEvent(entrance.getGameRoomId(), entrance.getUserId()));
                            },
                            gameRoom::close
                    );
        }

        userService.moveUserToLobby(userId);

        /**
         * 250519 게임방 퇴장시 event 발행
         */
        eventPublisher.publishEvent(new GameRoomExitEvent(gameRoom.getId(), userId));
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
        GameRoomEntrance targetEntrance = gameRoomEntranceRepository.findById(gameRoomEntranceId)
                .orElseThrow(GameRoomEntranceNotFoundException::new);

        if (!targetEntrance.getUserId().equals(userId)) {
            log.error("다른 유저의 정보 접근 시도");
            log.error("userId = {} ===>>> gameRoomEntranceId = {}", userId, gameRoomEntranceId);
            throw new AbnormalAccessException();
        }

        if (targetEntrance.getRole() == HOST) {
            log.debug("호스트는 레디 상태를 변경할 수 없습니다.");
            throw new GameRoomHostCanNotChangeReadyException();
        }

        targetEntrance.changeReady(ready);
        gameRoomEntranceRepository.save(targetEntrance);

        log.debug("게임 방 레디 변경 저장 = {}", targetEntrance.getId());

        eventPublisher.publishEvent(new UserReadyChanged(targetEntrance.getGameRoomId(), userId, ready));

        return ready;
    }
}
