package com.bb.webcanvasservice.domain.game.service;

import com.bb.webcanvasservice.common.exception.AbnormalAccessException;
import com.bb.webcanvasservice.domain.game.GameProperties;
import com.bb.webcanvasservice.domain.game.dto.response.GameRoomEntranceInfoResponse;
import com.bb.webcanvasservice.domain.game.entity.GameRoom;
import com.bb.webcanvasservice.domain.game.entity.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomEntranceState;
import com.bb.webcanvasservice.domain.game.event.GameRoomExitEvent;
import com.bb.webcanvasservice.domain.game.event.GameRoomHostChangedEvent;
import com.bb.webcanvasservice.domain.game.event.UserReadyChanged;
import com.bb.webcanvasservice.domain.game.exception.GameRoomEntranceNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.GameRoomHostCanNotChangeReadyException;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.enums.UserStateCode;
import com.bb.webcanvasservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.bb.webcanvasservice.domain.game.enums.GameRoomRole.HOST;

/**
 * 게임 방과 관련된 비즈니스 로직을 처리하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoomInnerService {

    /**
     * 레포지토리
     */
    private final GameRoomRepository gameRoomRepository;
    private final GameRoomEntranceRepository gameRoomEntranceRepository;

    /**
     * 서비스
     */
    private final UserService userService;
    
    /**
     * 설정 변수
     */
    private final GameProperties gameProperties;

    /**
     * 이벤트 퍼블리셔
     */
    private final ApplicationEventPublisher applicationEventPublisher;


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
    public GameRoomEntranceInfoResponse findEnteredGameRoomInfo(Long userId) {
        GameRoomEntrance userEntrance = gameRoomEntranceRepository.findGameRoomEntranceByUserId(userId, List.of(GameRoomEntranceState.WAITING, GameRoomEntranceState.PLAYING))
                .orElseThrow(GameRoomEntranceNotFoundException::new);


        GameRoom gameRoom = userEntrance.getGameRoom();

        AtomicInteger index = new AtomicInteger(0);

        List<GameRoomEntrance> gameRoomEntrances = gameRoomEntranceRepository
                .findGameRoomEntrancesByGameRoomIdAndStates(gameRoom.getId(), List.of(GameRoomEntranceState.WAITING, GameRoomEntranceState.PLAYING));

        List<GameRoomEntranceInfoResponse.EnteredUserSummary> enteredUserSummaries = gameRoomEntrances
                .stream()
                .map(gameRoomEntrance ->
                        new GameRoomEntranceInfoResponse.EnteredUserSummary(
                                gameRoomEntrance.getUser().getId(),
                                gameProperties.gameRoomUserColors().get(index.getAndIncrement()),
                                gameRoomEntrance.getNickname(),
                                userEntrance.getRole(),
                                gameRoomEntrance.isReady()
                        )
                )
                .collect(Collectors.toList());
        return new GameRoomEntranceInfoResponse(
                gameRoom.getId(),
                userEntrance.getId(),
                enteredUserSummaries,
                gameRoom.getState(),
                enteredUserSummaries.stream().filter(enteredUserSummary -> enteredUserSummary.userId().equals(userId)).findFirst().get()
        );
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
        GameRoomEntrance targetEntrance = gameRoomEntranceRepository.findById(gameRoomEntranceId)
                .orElseThrow(GameRoomEntranceNotFoundException::new);

        if (!targetEntrance.getUser().getId().equals(userId)) {
            log.error("대상 게임 방 입장 기록과 요청 유저가 다릅니다.");
            throw new AbnormalAccessException();
        }

        targetEntrance.exit();

        GameRoom gameRoom = targetEntrance.getGameRoom();
        List<GameRoomEntrance> entrances = gameRoomEntranceRepository
                .findGameRoomEntrancesByGameRoomIdAndState(gameRoom.getId(), GameRoomEntranceState.WAITING);

        if (entrances.isEmpty()) {
            gameRoom.close();
        } else if (targetEntrance.getRole() == HOST) {
            /**
             * 250522
             * 퇴장 요청을 보낸 유저가 HOST일 경우 남은 유저 중 제일 처음 입장한 유저가 HOST가 된다.
             */
            entrances.stream()
                    .filter(entrance -> entrance.getId() != gameRoomEntranceId)
                    .findFirst()
                    .ifPresentOrElse(
                            entrance -> {
                                entrance.changeRole(HOST);
                                // HOST changed event 발행
                                applicationEventPublisher.publishEvent(new GameRoomHostChangedEvent(entrance.getGameRoom().getId(), entrance.getUser().getId()));
                            },
                            () -> gameRoom.close()
                    );
        }

        userService.changeUserState(userId, UserStateCode.IN_LOBBY);
        
        /**
         * 250519 게임방 퇴장시 event 발행
         */
        applicationEventPublisher.publishEvent(new GameRoomExitEvent(gameRoom.getId(), userId));
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

        if (!targetEntrance.getUser().getId().equals(userId)) {
            log.error("다른 유저의 정보 접근 시도");
            log.error("userId = {} ===>>> gameRoomEntranceId = {}", userId, gameRoomEntranceId);
            throw new AbnormalAccessException();
        }

        if (targetEntrance.getRole() == HOST) {
            log.debug("호스트는 레디 상태를 변경할 수 없습니다.");
            throw new GameRoomHostCanNotChangeReadyException();
        }

        targetEntrance.changeReady(ready);
        applicationEventPublisher.publishEvent(new UserReadyChanged(targetEntrance.getGameRoom().getId(), userId, ready));

        return ready;
    }

}
