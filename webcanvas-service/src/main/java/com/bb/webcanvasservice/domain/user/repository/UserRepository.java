package com.bb.webcanvasservice.domain.user.repository;

import com.bb.webcanvasservice.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 게임 유저의 persitence layer를 담당하는 레포지토리 클래스
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 클라이언트 fingerprint로 등록된 유저 조회
     * @param fingerprint
     * @return
     */
    @Query("""
            select  u
            from    User u
            where   u.fingerprint = :fingerprint
            """)
    Optional<User> findByFingerprint(@Param("fingerprint") String fingerprint);
}
