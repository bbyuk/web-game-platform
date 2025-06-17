package com.bb.webcanvasservice.unit.domain.user;

import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.model.UserState;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserJpaRepository;
import com.bb.webcanvasservice.user.domain.service.UserService;
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
@DisplayName("[unit] [service] 유저 서비스 단위테스트")
class UserServiceTest {
    @Mock
    private UserJpaRepository userJpaRepository;
    @InjectMocks
    private UserService userService;


    @BeforeEach
    public void setMockUser() throws Exception {

    }

    @Test
    @DisplayName("클라이언트 fingerprint로 등록된 유저 조회 후 없을 시 유저 생성")
    void findUserByUserToken() throws Exception {
        final String fingerprint = UUID.randomUUID().toString();

        UserJpaEntity mockUser = createMockUser(fingerprint);

        Mockito.when(userJpaRepository.findByFingerprint(fingerprint))
                .thenReturn(Optional.empty());
        Mockito.when(userJpaRepository.save(any())).thenReturn(mockUser);

        // when
        User createdUser = userService.findOrCreateUser(fingerprint);

        // then
        Assertions.assertThat(createdUser).usingRecursiveComparison().isEqualTo(mockUser);
    }


    private static UserJpaEntity createMockUser(String userToken) throws NoSuchFieldException, IllegalAccessException {
        UserJpaEntity savedUser = new UserJpaEntity(1L, "fingerprint", "refreshToken", UserState.IN_LOBBY);
        Field idField = UserJpaEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(savedUser, 1L);
        return savedUser;
    }

}