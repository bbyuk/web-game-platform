package com.bb.webcanvasservice.domain.dictionary.repository;

import com.bb.webcanvasservice.domain.dictionary.model.Language;
import com.bb.webcanvasservice.domain.dictionary.model.PartOfSpeech;
import com.bb.webcanvasservice.domain.dictionary.model.Word;

import java.util.List;
import java.util.Optional;

public interface WordRepository {
    int saveInBatch(List<Word> words);
    Optional<Word> findRandomWordByLanguageAndPos(Language language, PartOfSpeech pos);

}
