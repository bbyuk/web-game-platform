package com.bb.webcanvasservice.domain.user;

import com.bb.webcanvasservice.common.exception.AlreadyExistsException;
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


    private final UserRepository userRepository;

    /**
     * 클라이언트 fingerprint로 등록된 유저를 조회 후 없을 시 유저 생성 후 리턴
     *
     * @param fingerprint
     * @return
     */
    @Transactional
    public User findOrCreateUser(String fingerprint) {
        return userRepository.findByFingerprint(fingerprint)
                .orElseGet(() -> createUser(fingerprint));
    }


    /**
     * 유저 ID로 유저를 조회해 리턴한다.
     * 찾지 못할 시 UserNotFoundException throw
     *
     * @param userId User 엔티티의 ID
     * @return User
     */
    @Transactional(readOnly = true)
    public User findUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾지 못했습니다."));
    }


    /**
     * 클라이언트의 fingerprint로 유저를 생성하고 저장 후 리턴한다.
     *
     * @param fingerprint
     * @return
     */
    @Transactional
    public User createUser(String fingerprint) {
        userRepository.findByFingerprint(fingerprint)
                .ifPresent(
                        user -> {
                            throw new AlreadyExistsException("이미 등록된 fingerprint 입니다. 관리자에게 문의해주세요.");
                        }
                );

        return userRepository.save(new User(fingerprint));
    }


}
