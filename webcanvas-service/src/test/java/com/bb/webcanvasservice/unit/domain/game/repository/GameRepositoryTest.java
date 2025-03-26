package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.common.RandomCodeGenerator;
import com.bb.webcanvasservice.domain.game.GameRoom;
import com.bb.webcanvasservice.domain.game.GameRoomEntrance;
import com.bb.webcanvasservice.domain.game.enums.GameRoomState;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest // JPA 관련 컴포넌트만 로드하여 테스트
class GameRepositoryTest {

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRoomEntranceRepository gameRoomEntranceRepository;


    private final String testUserToken = UUID.randomUUID().toString();

    private User testUser;

    @BeforeEach
    public void setTestData() {
        testUser = userRepository.save(new User(testUserToken));
    }


    @Test
    @DisplayName("유저 토큰으로 유저가 현재 입장한 방 조회 - 데이터 존재할 시 성공")
    public void findNotClosedRoomByUserToken() {
        // given
        String enteredRoomCode = RandomCodeGenerator.generate(10);
        String otherRoomCode = RandomCodeGenerator.generate(10);

        GameRoom enteredRoom = new GameRoom(GameRoomState.WAITING, enteredRoomCode);
        GameRoom otherRoom = new GameRoom(GameRoomState.WAITING, otherRoomCode);

        gameRoomRepository.save(enteredRoom);
        gameRoomRepository.save(otherRoom);

        GameRoomEntrance gameRoomEntrance = new GameRoomEntrance(enteredRoom, testUser);
        gameRoomEntranceRepository.save(gameRoomEntrance);

        enteredRoom.addEntrance(gameRoomEntrance);

        // when
        Optional<GameRoom> queryResult = gameRoomRepository.findNotClosedGameRoomByUserToken(testUserToken);

        // then
        Assertions.assertThat(queryResult.isPresent()).isTrue();
        Assertions.assertThat(queryResult.get().getJoinCode()).isEqualTo(enteredRoomCode);
    }

    @Test
    @DisplayName("활성 상태인 게임 방의 입장 코드와 충돌이 발생했을 때 true 리턴")
    public void returnTrueWhenJoinCodeConflict() {
        // given
        String testRoomCode = RandomCodeGenerator.generate(10);
        GameRoom playingGameRoom = new GameRoom(GameRoomState.PLAYING, testRoomCode);

        gameRoomRepository.save(playingGameRoom);

        // when
        boolean result = gameRoomRepository.existsJoinCodeConflictOnActiveGameRoom(testRoomCode);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("쿼리 요청 유저가 입장한 방이 있다면 true 리턴")
    public void returnTrueWhenUserEnteredAnyRoom() {
        // given
        Long testUserId = testUser.getId();

        GameRoom waitingRoom = new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(10));
        GameRoom playingRoom = new GameRoom(GameRoomState.PLAYING, RandomCodeGenerator.generate(10));

        gameRoomRepository.save(waitingRoom);
        gameRoomRepository.save(playingRoom);

        // when
        Assertions.assertThat(gameRoomEntranceRepository.existsGameRoomEntranceByUserId(testUserId)).isFalse();

        // then

    }

    @Test
    @DisplayName("쿼리 요청 유저가 입장한 방이 없다면 false 리턴")
    public void returnFalseWhenUserNotEnteredAnyRoom() {
        // given
        Long testUserId = testUser.getId();

        GameRoom waitingRoom = new GameRoom(GameRoomState.WAITING, RandomCodeGenerator.generate(10));
        GameRoom playingRoom = new GameRoom(GameRoomState.PLAYING, RandomCodeGenerator.generate(10));

        gameRoomRepository.save(waitingRoom);
        gameRoomRepository.save(playingRoom);

        GameRoomEntrance gameRoomEntrance = new GameRoomEntrance(waitingRoom, testUser);
        gameRoomEntranceRepository.save(gameRoomEntrance);

        // when
        boolean result = gameRoomEntranceRepository.existsGameRoomEntranceByUserId(testUserId);

        // then
        Assertions.assertThat(result).isTrue();
    }
}