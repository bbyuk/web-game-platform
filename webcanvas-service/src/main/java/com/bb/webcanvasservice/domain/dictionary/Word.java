package com.bb.webcanvasservice.domain.dictionary;

import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 닉네임, 게임 제시어 등에서 사용되는 한글 단어를 저장하는 사전
 */
@Entity
@Getter
@Table(name = "words")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Word {

    @Id
    @GeneratedValue
    @Column(name = "id")
    /**
     * 사전 ID
     */
    private Long id;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "value")
    /**
     * 단어 값
     */
    private String value;

    @Column(name = "word_index")
    /**
     * 단어 인덱스
     */
    private Long index;

    @Column(name = "pos")
    @Enumerated(EnumType.STRING)
    /**
     * 품사
     */
    private PartOfSpeech pos;

    public Word(Language language, String value, Long index, PartOfSpeech pos) {
        this.language = language;
        this.value = value;
        this.index = index;
        this.pos = pos;
    }
}
