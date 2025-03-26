package com.bb.webcanvasservice.domain.game;

import com.bb.webcanvasservice.common.RandomCodeGenerator;
import com.bb.webcanvasservice.domain.game.exception.AlreadyEnteredRoomException;
import com.bb.webcanvasservice.domain.game.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.IllegalGameRoomStateException;
import com.bb.webcanvasservice.domain.game.exception.JoinCodeNotGeneratedException;
import com.bb.webcanvasservice.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게임 방과 게임 세션 등 게임과 관련된 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class GameService {

    /**
     * 게임 방의 입장 코드 길이
     */
    private final int JOIN_CODE_LENGTH = 10;
    private final int GAME_ROOM_MAX_CAPACITY = 8;

    /**
     * 서비스 주입
     */
    private final UserService userService;

    /**
     * 레포지토리 주입
     */
    private final GameRoomRepository gameRoomRepository;
    private final GameRoomEntranceRepository gameRoomEntranceRepository;

    /**
     * 유저 토큰으로 요청자를 식별해, 요청자가 입장한 게임 방을 리턴한다.
     * 현재 입장한 게임 방이 없을 경우, GameRoomNotFoundException 발생
     * @param userToken
     * @return
     */
    @Transactional
    public GameRoom findGameRoomByUserToken(String userToken) {
        return gameRoomRepository.findByGameRoomStateNotMatchedAndUserToken(GameRoomState.CLOSED, userToken)
                .orElseThrow(() -> new GameRoomNotFoundException("현재 입장한 방을 찾읈 수 없습니다."));
    }


    /**
     * 유저 토큰으로 요청자를 식별해, 요청자를 호스트로 하는 방을 새로 생성해 게임 방을 리턴한다.
     * @param hostUserToken
     * @return gameRoomId
     */
    @Transactional
    public Long createGameRoom(String hostUserToken) {
        /**
         * join code 생성
         *
         * 1. 랜덤 코드 생성
         * 2. 랜덤 코드 충돌 확인
         *      2.1. 현재 방의 상태가 closed가 아닌 게임 방 중 생성된 랜덤코드와 동일한 joinCode를 가진 게임 방이 있는지 조회
         *      2.2. 충돌 시 1번 로직으로 이동
         *      2.3. 통과시 해당 랜덤코드를 새로 생성할 방의 joinCode로 채택
         */

        boolean collide = true;
        String joinCode = "";

        while(collide) {
            String randomCode = RandomCodeGenerator.generate(JOIN_CODE_LENGTH);
            collide = gameRoomRepository.findByGameRoomStateNotMatchedAndJoinCode(GameRoomState.CLOSED, randomCode)
                    .isPresent();

            if (!collide) {
                joinCode = randomCode;
            }
        }

        if (joinCode.isEmpty()) {
            throw new JoinCodeNotGeneratedException("join code가 정상적으로 생성되지 않았습니다.");
        }

        /**
         * GameRoom 생성
         */
        GameRoom newGameRoom = gameRoomRepository.save(new GameRoom(GameRoomState.WAITING, joinCode));

        /**
         * GameRoom 입장
         */
        enterGameRoom(newGameRoom.getId(), userService.findUserByUserToken(hostUserToken).getId());


        return newGameRoom.getId();
    }

    /**
     * 게임 방에 유저를 입장시킨다.
     *
     * - 입장시키려는 유저가 현재 아무 방에도 접속하지 않은 상태여야 한다. -> 방에서 나갈 시 삭제 처리
     * - 입장하려는 방의 상태가 WAITING이어야 한다.
     * - 입장하려는 방에 접속한 유저 세션의 수(entrances)는 최대 8이다.
     * @param gameRoomId
     * @param userId
     * @return gameRoomEntranceId
     */
    @Transactional
    public Long enterGameRoom(Long gameRoomId, Long userId) {
        if (gameRoomEntranceRepository.existsGameRoomEntranceByUserId(userId)) {
            throw new AlreadyEnteredRoomException("이미 입장한 방이 있습니다.");
        }

        GameRoom targetGameRoom = gameRoomRepository.findByIdWithEntrances(gameRoomId).orElseThrow(() -> new GameRoomNotFoundException("게임 방을 찾을 수 없습니다."));
        if (!targetGameRoom.getState().equals(GameRoomState.WAITING)) {
            throw new IllegalGameRoomStateException("방이 현재 입장할 수 없는 상태입니다.");
        }

        if (targetGameRoom.getEntrances().size() >= GAME_ROOM_MAX_CAPACITY) {
            throw new IllegalGameRoomStateException("방의 정원이 모두 찼습니다.");
        }


        GameRoomEntrance gameRoomEntrance =
                new GameRoomEntrance(
                        targetGameRoom
                        , userService.findUserByUserId(userId));

        GameRoomEntrance newGameRoomEntrance = gameRoomEntranceRepository.save(gameRoomEntrance);



        return newGameRoomEntrance.getId();
    }
}
