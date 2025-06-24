package com.bb.webcanvasservice.medium.user.domain.repository;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    UserRepository userRepository;

    @Test
    @DisplayName("유저 ID로 조회")
    void 유저_ID로_조회() throws Exception {
        // given
        User user = User.create(FingerprintGenerator.generate());

        // when
        User savedUser = userRepository.save(user);

        // then
        userRepository.findById(savedUser.getId())
                .ifPresent(findUser ->
                                Assertions
                                        .assertThat(findUser)
                                        .usingRecursiveComparison()
                        .isEqualTo(savedUser)
                );
    }
}