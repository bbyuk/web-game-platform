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

    @Column(name = "word_type")
    /**
     * 문자 종류 (ex : 한자어, 혼합어 등)
     */
    private String type;

    @Column(name = "word_unit")
    /**
     * 단어 유닛 -> (ex : 어휘, 구 등)
     */
    private String unit;

    @Column(name = "pos")
    @Enumerated(EnumType.STRING)
    /**
     * 품사
     */
    private PartOfSpeech partOfSpeech;


}
