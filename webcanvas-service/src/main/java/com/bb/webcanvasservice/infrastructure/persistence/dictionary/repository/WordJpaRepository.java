package com.bb.webcanvasservice.infrastructure.persistence.dictionary.repository;

import com.bb.webcanvasservice.infrastructure.persistence.dictionary.entity.WordJpaEntity;
import com.bb.webcanvasservice.domain.dictionary.model.Language;
import com.bb.webcanvasservice.domain.dictionary.model.PartOfSpeech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WordJpaRepository extends JpaRepository<WordJpaEntity, Long> {

}
