package com.bb.webcanvasservice.user.application.config;

import com.bb.webcanvasservice.user.application.repository.UserRepository;
import com.bb.webcanvasservice.user.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 유저 도메인 서비스 설정
 */
@Configuration
@RequiredArgsConstructor
public class UserApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserService userService() {
        return new UserService(userRepository);
    }
}
