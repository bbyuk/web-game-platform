package com.bb.webcanvasservice.unit.domain.game.repository;

import com.bb.webcanvasservice.config.JpaConfig;
import com.bb.webcanvasservice.domain.game.repository.GameRoomEntranceRepository;
import com.bb.webcanvasservice.domain.game.repository.GameRoomRepository;
import com.bb.webcanvasservice.domain.game.repository.GameSessionRepository;
import com.bb.webcanvasservice.infrastructure.persistence.user.UserJpaRepository;
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
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private GameRoomEntranceRepository gameRoomEntranceRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;



}