package com.bb.webcanvasservice.unit.domain.user;

import com.bb.webcanvasservice.domain.user.User;
import com.bb.webcanvasservice.domain.user.UserRepository;
import com.bb.webcanvasservice.domain.user.UserService;
import com.bb.webcanvasservice.domain.user.exception.UserNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;


    @BeforeEach
    public void setMockUser() throws Exception {

    }

    @Test
    @DisplayName("User Token으로 유저 조회")
    void findUserByUserToken() throws Exception {

        final String userToken = UUID.randomUUID().toString();

        User mockUser = createMockUser(userToken);
        Mockito.when(userRepository.findByUserToken(userToken))
                .thenReturn(Optional.of(mockUser));

        // when
        User findUser = userService.findUserByUserToken(userToken);

        // then
        Assertions.assertThat(findUser.getUserToken()).isEqualTo(userToken);
    }

    @Test
    @DisplayName("요청한 쿼리에 대한 유저를 찾지 못한 경우 UserNotFoundException throw")
    public void whenUserNotFound() throws Exception {
        // given
        String wrongToken = "wrong_token";
        Mockito.when(userRepository.findByUserToken(wrongToken))
                .thenReturn(Optional.empty());

        // when
        Assertions.assertThatThrownBy(() -> userService.findUserByUserToken(wrongToken))
                .isInstanceOf(UserNotFoundException.class);

        // then
    }

    private static User createMockUser(String userToken) throws NoSuchFieldException, IllegalAccessException {
        User savedUser = new User(userToken);
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(savedUser, 1L);
        return savedUser;
    }

}