package com.bb.webcanvasservice.medium.user.infrastructure.persistence.repository;

import com.bb.webcanvasservice.common.util.FingerprintGenerator;
import com.bb.webcanvasservice.config.JpaConfig;
import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;
import com.bb.webcanvasservice.user.infrastructure.persistence.repository.UserRepositoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import({JpaConfig.class, UserRepositoryImpl.class})
@Transactional
@DisplayName("[medium] [user] [persistence] User Repository 영속성 테스트")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유저 ID로 조회")
    void 유저_ID로_조회() throws Exception {
        // given
        User user = User.create(FingerprintGenerator.generate());

        // when
        User savedUser = userRepository.save(user);

        // then
        userRepository.findById(savedUser.id())
                .ifPresent(findUser ->
                        Assertions
                                .assertThat(findUser)
                                .usingRecursiveComparison()
                                .isEqualTo(savedUser)
                );
    }

    @Test
    @DisplayName("클라이언트의 Fingerprint로 등록된 유저 조회")
    void fingerprint로_조회() throws Exception {
        // given
        String fingerprint = FingerprintGenerator.generate();
        User savedUser = userRepository.save(User.create(fingerprint));

        // when
        userRepository.findByFingerprint(fingerprint)
                .ifPresent(findUser ->
                        Assertions.assertThat(findUser)
                                .usingRecursiveComparison()
                                .isEqualTo(savedUser)
                );

        // then

    }
}