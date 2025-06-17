package com.bb.webcanvasservice.user.infrastructure.persistence.repository;

import com.bb.webcanvasservice.user.domain.model.UserState;
import com.bb.webcanvasservice.user.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 게임 유저의 persitence layer를 담당하는 JPA 레포지토리 클래스
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    /**
     * 클라이언트 fingerprint로 등록된 유저 조회
     * @param fingerprint
     * @return
     */
    @Query("""
            select  u
            from    UserJpaEntity u
            where   u.fingerprint = :fingerprint
            """)
    Optional<UserJpaEntity> findByFingerprint(@Param("fingerprint") String fingerprint);

    /**
     * 유저 상태를 조회한다.
     * @param userId
     * @return
     */
    @Query("""
            select  u.state
            from    UserJpaEntity u
            where   u.id = :userId
            """)
    UserState findUserState(@Param("userId") Long userId);
}
