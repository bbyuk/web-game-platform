package com.bb.webcanvasservice.game.domain.port.dictionary;

/**
 * game -> dictionary 조회 포트
 */
public interface GameDictionaryQueryPort {

    /**
     * 무작위 한국어 명사 뽑기
     * @return 무작위 한국어 명사
     */
    String drawRandomKoreanNoun();

    /**
     * 무작위 한국어 형용사 뽑기
     * @return 무작위 한국어 형용사
     */
    String drawRandomKoreanAdjective();
}
