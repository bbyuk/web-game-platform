package com.bb.webcanvasservice.domain.dictionary.repository;

import com.bb.webcanvasservice.domain.dictionary.entity.Word;
import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;

import java.util.Optional;

/**
 * Word 커스텀 레포지토리
 */
public interface WordCustomRepository {

    Optional<Word> findRandomWordByLanguageAndPos(Language language, PartOfSpeech pos);
}
