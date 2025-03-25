package com.bb.webcanvasservice.domain.game;

import com.bb.webcanvasservice.common.RandomCodeGenerator;
import com.bb.webcanvasservice.domain.game.exception.GameRoomNotFoundException;
import com.bb.webcanvasservice.domain.game.exception.JoinCodeNotGeneratedException;
import com.bb.webcanvasservice.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게임 방과 게임 세션 등 게임과 관련된 서비스를 처리하는 클래스
 */
@Service
@RequiredArgsConstructor
public class GameService {

    private final int JOIN_CODE_LENGTH = 10;

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
     * 유저 토큰으로 요청자를 식별해, 요청자가 입장한 게임 방을 리턴한다.
     * 현재 입장한 게임 방이 없을 경우, GameRoomNotFoundException 발생
     * @param userToken
     * @return
     */
    @Transactional
    public GameRoom findGameRoomByUserToken(String userToken) {
        return gameRoomRepository.findByGameRoomStatusNotMatchedAndUserToken(GameRoomStatus.CLOSED, userToken)
                .orElseThrow(() -> new GameRoomNotFoundException("현재 입장한 방을 찾읈 수 없습니다."));
    }

    /**
     * 유저 토큰으로 요청자를 식별해, 요청자를 호스트로 하는 방을 새로 생성해 게임 방을 리턴한다.
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
            collide = gameRoomRepository.findByGameRoomStatusNotMatchedAndJoinCode(GameRoomStatus.CLOSED, randomCode)
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
        GameRoom newGameRoom = gameRoomRepository.save(new GameRoom(GameRoomStatus.WAITING, joinCode));

        /**
         * TODO 게임 방 입장 Entity 생성 및 저장
         */
        GameRoomEntrance gameRoomEntrance = new GameRoomEntrance(newGameRoom, userService.findUserByUserToken(hostUserToken));
        gameRoomEntranceRepository.save(gameRoomEntrance);

        return newGameRoom.getId();
    }
}
