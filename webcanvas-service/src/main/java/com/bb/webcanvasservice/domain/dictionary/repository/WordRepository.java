package com.bb.webcanvasservice.domain.dictionary.repository;

import com.bb.webcanvasservice.domain.dictionary.entity.Word;
import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long>, WordBatchRepository, WordCustomRepository {

    @Query("""
        select  w
        from    Word w
        where   w.language = :language
        and     w.pos = :pos
        and     w.index = :index
        """)
    Optional<Word> findByLanguageAndPosAndIndex(@Param("language")Language language, @Param("pos")PartOfSpeech pos, @Param("index") Long index);
}
