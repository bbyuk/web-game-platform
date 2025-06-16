package com.bb.webcanvasservice.domain.dictionary.service;

import com.bb.webcanvasservice.domain.dictionary.exception.WordNotFoundException;
import com.bb.webcanvasservice.domain.dictionary.model.Language;
import com.bb.webcanvasservice.domain.dictionary.model.PartOfSpeech;
import com.bb.webcanvasservice.domain.dictionary.repository.WordRepository;

/**
 * https://opendict.korean.go.kr/main
 * 사전을 파싱해 DB에 적재하기 위해 제공하는 서비스
 * TODO 형용사 어미 ~한 ~된 등으로 변경 로직 구상
 */
public class DictionaryService {

    private final WordRepository wordRepository;

    public DictionaryService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }


    /**
     * 랜덤한 단어의 값을 조회해온다.
     * @param language 대상 언어
     * @param pos 품사
     * @return
     */
    public String drawRandomWordValue(Language language, PartOfSpeech pos) {
        return wordRepository.findRandomWordByLanguageAndPos(language, pos)
                .orElseThrow(WordNotFoundException::new)
                .getValue();
    }

}
