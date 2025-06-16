package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.common.config.JpaConfig;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomEntranceJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameRoomJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.game.repository.GameSessionJpaRepository;
import com.bb.webcanvasservice.infrastructure.persistence.user.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@DataJpaTest
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("[unit] [persistence] 게임 플레이 관련 GameSession Repository 단위테스트")
class GameSessionRepositoryTest {

    @Autowired
    private GameSessionJpaRepository gameSessionRepository;

    @Autowired
    private GameRoomJpaRepository gameRoomRepository;

    @Autowired
    private GameRoomEntranceJpaRepository gameRoomEntranceRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;



}