package com.bb.webcanvasservice.domain.user;

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

    @Query("""
            select u
            from User u
            where u.userToken = :userToken
            """)
    Optional<User> findByUserToken(@Param("userToken") String userToken);
}
