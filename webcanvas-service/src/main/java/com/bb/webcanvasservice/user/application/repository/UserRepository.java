package com.bb.webcanvasservice.user.application.repository;

import com.bb.webcanvasservice.user.domain.model.User;
import com.bb.webcanvasservice.user.domain.model.UserState;

import java.util.List;
import java.util.Optional;

/**
 * user domain repository
 */
public interface UserRepository {

    /**
     * 유저 ID로 유저를 조회한다.
     * @param userId 유저 ID
     * @return 유저
     */
    Optional<User> findById(Long userId);

    /**
     * 유저를 저장한다.
     * @param user 유저 객체
     * @return 저장된 유저
     */
    User save(User user);
    
    /**
     * 클라이언트 fingerprint로 등록된 유저 조회
     * @param fingerprint 서버에서 생성된 유저 fingerprint
     * @return 유저
     */
    Optional<User> findByFingerprint(String fingerprint);

    /**
     * 유저 상태를 조회한다.
     * @param userId 유저 ID
     * @return 유저 상태 코드
     */
    UserState findUserState(Long userId);

    /**
     * 유저들의 상태 코드를 일괄 업데이트한다.
     * @param userIds 대상 유저 ID 목록
     * @param state 상태
     */
    void updateUsersStates(List<Long> userIds, UserState state);
}
