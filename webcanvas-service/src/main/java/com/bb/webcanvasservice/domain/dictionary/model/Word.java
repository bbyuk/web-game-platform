package com.bb.webcanvasservice.domain.dictionary.model;

/**
 * Dictionary service word 도메인 모델
 */
public class Word {

    /**
     * 사전 ID
     */
    private final Long id;

    private final Language language;

    /**
     * 단어 값
     */
    private final String value;

    /**
     * 단어 인덱스
     */
    private final Long index;

    /**
     * 품사
     */
    private final PartOfSpeech pos;

    /**
     * 새 단어 객체를 생성한다.
     * @param language 언어
     * @param value 값
     * @param index 인덱스
     * @param pos 품사
     * @return 단어 객체
     */
    public static Word createNewWord(Language language, String value, Long index, PartOfSpeech pos) {
        return new Word(null ,language, value, index, pos);
    }

    public Word(Long id, Language language, String value, Long index, PartOfSpeech pos) {
        this.id = id;
        this.language = language;
        this.value = value;
        this.index = index;
        this.pos = pos;
    }

    public Long getId() {
        return id;
    }

    public Language getLanguage() {
        return language;
    }

    public String getValue() {
        return value;
    }

    public Long getIndex() {
        return index;
    }

    public PartOfSpeech getPos() {
        return pos;
    }
}
