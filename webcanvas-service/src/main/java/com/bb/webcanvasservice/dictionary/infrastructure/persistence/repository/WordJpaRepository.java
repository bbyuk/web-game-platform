package com.bb.webcanvasservice.dictionary.infrastructure.persistence.repository;

import com.bb.webcanvasservice.dictionary.infrastructure.persistence.entity.WordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordJpaRepository extends JpaRepository<WordJpaEntity, Long> {

}
