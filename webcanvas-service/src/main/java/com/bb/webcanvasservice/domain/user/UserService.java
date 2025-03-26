package com.bb.webcanvasservice.domain.user;

import com.bb.webcanvasservice.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게임 유저에 대한 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class UserService {

    /**
     * 레포지토리 주입
     */
    private final UserRepository userRepository;

    /**
     * 유저 토큰으로 유저를 조회해 리턴한다.
     * 찾지 못할 시 UserNotFoundException throw
     * @param userToken
     * @return User
     */
    @Transactional(readOnly = true)
    public User findUserByUserToken(String userToken) {
        return userRepository.findByUserToken(userToken)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾지 못했습니다."));
    }

    /**
     * 유저 ID로 유저를 조회해 리턴한다.
     * 찾지 못할 시 UserNotFoundException throw
     * @param userId
     * @return User
     */
    @Transactional(readOnly = true)
    public User findUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾지 못했습니다."));
    }
}
