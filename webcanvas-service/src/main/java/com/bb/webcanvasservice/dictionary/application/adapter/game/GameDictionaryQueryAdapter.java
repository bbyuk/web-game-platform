package com.bb.webcanvasservice.dictionary.application.adapter.game;

import com.bb.webcanvasservice.dictionary.application.service.DictionaryService;
import com.bb.webcanvasservice.dictionary.domain.model.Language;
import com.bb.webcanvasservice.dictionary.domain.model.PartOfSpeech;
import com.bb.webcanvasservice.game.application.port.dictionary.DictionaryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * game -> dictionary 조회 포트 어댑터
 */
@Service
@RequiredArgsConstructor
public class GameDictionaryQueryAdapter implements DictionaryQueryPort {

    private final DictionaryService dictionaryService;

    @Override
    public String drawRandomKoreanNoun() {
        return dictionaryService.drawRandomWordValue(Language.KOREAN, PartOfSpeech.NOUN);
    }

    @Override
    public String drawRandomKoreanAdjective() {
        return dictionaryService.drawRandomWordValue(Language.KOREAN, PartOfSpeech.ADJECTIVE);
    }
}
