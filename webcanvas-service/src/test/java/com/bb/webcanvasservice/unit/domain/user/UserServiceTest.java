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

import static org.mockito.ArgumentMatchers.any;

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
    @DisplayName("클라이언트 fingerprint로 등록된 유저 조회 후 없을 시 유저 생성")
    void findUserByUserToken() throws Exception {
        final String fingerprint = UUID.randomUUID().toString();

        User mockUser = createMockUser(fingerprint);

        Mockito.when(userRepository.findByFingerprint(fingerprint))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.save(any())).thenReturn(mockUser);

        // when
        User createdUser = userService.findOrCreateUser(fingerprint);

        // then
        Assertions.assertThat(createdUser).usingRecursiveComparison().isEqualTo(mockUser);
    }


    private static User createMockUser(String userToken) throws NoSuchFieldException, IllegalAccessException {
        User savedUser = new User(userToken);
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(savedUser, 1L);
        return savedUser;
    }

}