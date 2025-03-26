package com.bb.webcanvasservice.domain.game;

import com.bb.webcanvasservice.common.RandomCodeGenerator;
import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class GameServiceUnitTest {

    @Mock
    private GameRoomRepository gameRoomRepository;
    @Mock
    private GameRoomEntranceRepository gameRoomEntranceRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private GameService gameService;

    private final String testUserToken = UUID.randomUUID().toString();

    @Test
    @DisplayName("GameRoom에 입장시킨다.")
    public void enterGameRoom() throws Exception {
        // given
        User testUser = new User(testUserToken);
        setId(testUser, 1L);
        Mockito.when(userService.findUserByUserId(any(Long.class)))
                .thenReturn(testUser);

        String joinCode = RandomCodeGenerator.generate(10);
        GameRoom testGameRoom = new GameRoom(GameRoomState.WAITING, joinCode);
        setId(testGameRoom, 1L);

        Mockito.when(gameRoomRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(testGameRoom));

        GameRoomEntrance testGameRoomEntrance = new GameRoomEntrance(testGameRoom, testUser);
        setId(testGameRoomEntrance, 1L);

        Mockito.when(gameRoomEntranceRepository.save(any(GameRoomEntrance.class)))
                .thenReturn(testGameRoomEntrance);

        // when
        Long gameRoomEnterId = gameService.enterGameRoom(1L, 1L);

        // then
        Assertions.assertThat(gameRoomEnterId).isEqualTo(1L);
    }


    private void setId(Object entity, Long id) throws NoSuchFieldException, IllegalAccessException {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }
}