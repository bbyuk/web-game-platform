package com.bb.webcanvasservice.common.sequence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MySQL 시퀀스 테이블
 * 다른 도메인에서는 Repository 인터페이스를 통해 nextValue만 조회할 수 있도록 한다.
 */
@Entity
@Table(name = "sequences")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sequence {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "value")
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private Long value = 0L;

    Sequence(String name) {
        this.name = name;
    }

}
