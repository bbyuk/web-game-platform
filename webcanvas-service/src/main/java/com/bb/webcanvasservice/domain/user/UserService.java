package com.bb.webcanvasservice.domain.user;

import com.bb.webcanvasservice.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findUserByUserToken(String userToken) {
        return userRepository.findByUserToken(userToken)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾지 못했습니다."));
    }
}
