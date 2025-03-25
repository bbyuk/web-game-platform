package com.bb.webcanvasservice.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            select u
            from User u
            where u.userToken = :userToken
            """)
    Optional<User> findByUserToken(@Param("userToken") String userToken);
}
