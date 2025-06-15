package com.bb.webcanvasservice.domain.user.service;

import com.bb.webcanvasservice.domain.user.exception.AlreadyRegisteredUserException;
import com.bb.webcanvasservice.domain.user.exception.UserNotFoundException;
import com.bb.webcanvasservice.domain.user.model.User;
import com.bb.webcanvasservice.domain.user.model.UserStateCode;
import com.bb.webcanvasservice.domain.user.repository.UserRepository;

/**
 * 게임 유저에 대한 비즈니스 로직을 처리하는 서비스 클래스
 */
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 클라이언트 fingerprint로 등록된 유저를 조회 후 없을 시 유저 생성 후 리턴
     *
     * @param fingerprint
     * @return
     */
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
    public User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }


    /**
     * 생성된 fingerprint로 유저를 생성하고 저장 후 리턴한다.
     *
     * @param fingerprint
     * @return
     */
    public User createUser(String fingerprint) {
        userRepository.findByFingerprint(fingerprint)
                .ifPresent(
                        user -> {
                            throw new AlreadyRegisteredUserException();
                        }
                );

        return userRepository.save(User.createNewUser(fingerprint));
    }

    /**
     * 유저 상태를 게임 방으로 옮긴다.
     * @param userId 유저 ID
     */
    public void moveUserToRoom(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.changeState(UserStateCode.IN_ROOM);
        userRepository.save(user);
    }

    /**
     * 유저 상태를 로비로 옮긴다.
     * @param userId 유저 ID
     */
    public void moveUserToLobby(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.changeState(UserStateCode.IN_LOBBY);
        userRepository.save(user);
    }

    /**
     * 유저 상태를 게임중으로 변경한다.
     * @param userId 유저 ID
     */
    public void moveUserToGameSession(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.changeState(UserStateCode.IN_GAME);
        userRepository.save(user);
    }

    /**
     * 유저 상태를 조회한다.
     * @param userId
     * @return
     */
    public UserStateCode findUserState(Long userId) {
        return userRepository.findUserState(userId);
    }

    /**
     * 유저의 리프레쉬 토큰을 변경한다.
     * @param userId 대상 유저 ID
     * @param refreshToken 리프레쉬 토큰
     */
    public void updateRefreshToken(Long userId, String refreshToken) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
    }

}
