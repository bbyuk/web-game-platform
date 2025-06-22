package com.bb.webcanvasservice.user.infrastructure.config;

import com.bb.webcanvasservice.auth.domain.port.AuthUserCommandPort;
import com.bb.webcanvasservice.auth.domain.port.AuthUserQueryPort;
import com.bb.webcanvasservice.game.domain.port.user.GameUserCommandPort;
import com.bb.webcanvasservice.game.domain.port.user.GameUserQueryPort;
import com.bb.webcanvasservice.user.domain.adapter.auth.AuthUserCommandAdapter;
import com.bb.webcanvasservice.user.domain.adapter.auth.AuthUserQueryAdapter;
import com.bb.webcanvasservice.user.domain.adapter.game.GameUserCommandAdapter;
import com.bb.webcanvasservice.user.domain.adapter.game.GameUserQueryAdapter;
import com.bb.webcanvasservice.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 유저 도메인 어댑터 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
public class UserAdapterConfig {

    private final UserRepository userRepository;

    @Bean
    public GameUserCommandPort gameUserCommandPort() {
        return new GameUserCommandAdapter(userRepository);
    }

    @Bean
    public GameUserQueryPort gameUserQueryPort() {
        return new GameUserQueryAdapter();
    }

    @Bean
    public AuthUserCommandPort authUserCommandPort() {
        return new AuthUserCommandAdapter(userRepository);
    }

    @Bean
    public AuthUserQueryPort authUserQueryPort() {
        return new AuthUserQueryAdapter(userRepository);
    }

}
