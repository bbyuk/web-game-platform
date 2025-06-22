package com.bb.webcanvasservice.dictionary.domain.adapter.game;

import com.bb.webcanvasservice.dictionary.domain.exception.WordNotFoundException;
import com.bb.webcanvasservice.dictionary.domain.model.Language;
import com.bb.webcanvasservice.dictionary.domain.model.PartOfSpeech;
import com.bb.webcanvasservice.dictionary.domain.repository.WordRepository;
import com.bb.webcanvasservice.game.domain.port.dictionary.GameDictionaryQueryPort;

/**
 * game -> dictionary 조회 포트 어댑터
 */
public class GameDictionaryQueryAdapter implements GameDictionaryQueryPort {

    private final WordRepository wordRepository;

    public GameDictionaryQueryAdapter(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public String drawRandomKoreanNoun() {
        return wordRepository.findRandomWordByLanguageAndPos(
                        Language.KOREAN,
                        PartOfSpeech.NOUN)
                .orElseThrow(WordNotFoundException::new)
                .getValue();
    }

    @Override
    public String drawRandomKoreanAdjective() {
        return wordRepository.findRandomWordByLanguageAndPos(
                        Language.KOREAN,
                        PartOfSpeech.ADJECTIVE)
                .orElseThrow(WordNotFoundException::new)
                .getValue();
    }
}
