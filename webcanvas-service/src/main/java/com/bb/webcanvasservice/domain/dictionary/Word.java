package com.bb.webcanvasservice.domain.dictionary;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "word_unit")
    /**
     * 단어 유닛 -> (ex : 어휘, 구 등)
     */
    private String unit;

    @Column(name = "pos")
    /**
     * 품사
     */
    private String pos;

    @Setter
    @Column(name = "original_value")
    /**
     * 테스트용 컬럼
     */
    private String originalValue;

    public Word(String value, Long index, String unit, String pos) {
        this.value = value;
        this.index = index;
        this.unit = unit;
        this.pos = pos;
    }
}
