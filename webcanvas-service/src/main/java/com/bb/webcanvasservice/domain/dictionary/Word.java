package com.bb.webcanvasservice.domain.dictionary;

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

    @Column(name = "value")
    /**
     * 단어 값
     */
    private String value;

    @Column(name = "category")
    /**
     * 카테고리
     */
    private String category;

    @Column(name = "word_index")
    /**
     * 단어 인덱스
     */
    private Long index;

    @Column(name = "word_type1")
    /**
     * 문자 종류 (ex : 한자어, 혼합어 등)
     */
    private String type1;

    @Column(name = "word_type2")
    /**
     * 워드 타입 2
     */
    private String type2;

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


    public Word(String value, Long index, String category, String type1, String type2, String unit, String pos) {
        this.value = value;
        this.index = index;
        this.category = category;
        this.type1 = type1;
        this.type2 = type2;
        this.unit = unit;
        this.pos = pos;
    }
}
