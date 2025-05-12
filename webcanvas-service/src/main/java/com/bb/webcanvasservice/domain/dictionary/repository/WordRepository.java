package com.bb.webcanvasservice.domain.dictionary.repository;

import com.bb.webcanvasservice.domain.dictionary.Word;
import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long>, WordBatchRepository {

    @Query("""
        select  w
        from    Word w
        where   w.language = :language
        and     w.pos = :pos
        and     w.index = :index
        """)
    Optional<Word> findByLanguageAndPosAndIndex(@Param("language")Language language, @Param("pos")PartOfSpeech pos, @Param("index") Long index);
}
