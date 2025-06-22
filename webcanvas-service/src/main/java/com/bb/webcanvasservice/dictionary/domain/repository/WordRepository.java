package com.bb.webcanvasservice.dictionary.domain.repository;

import com.bb.webcanvasservice.dictionary.domain.model.Language;
import com.bb.webcanvasservice.dictionary.domain.model.PartOfSpeech;
import com.bb.webcanvasservice.dictionary.domain.model.Word;

import java.util.List;
import java.util.Optional;

public interface WordRepository {
    int saveInBatch(List<Word> words);
    Optional<Word> findRandomWordByLanguageAndPos(Language language, PartOfSpeech pos);

}
