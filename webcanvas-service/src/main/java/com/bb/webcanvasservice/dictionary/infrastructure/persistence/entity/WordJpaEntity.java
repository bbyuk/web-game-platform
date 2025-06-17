package com.bb.webcanvasservice.dictionary.infrastructure.persistence.entity;

import com.bb.webcanvasservice.dictionary.domain.model.Language;
import com.bb.webcanvasservice.dictionary.domain.model.PartOfSpeech;
import com.bb.webcanvasservice.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 닉네임, 게임 제시어 등에서 사용되는 한글 단어를 저장하는 사전
 */
@Entity
@Getter
@Table(name = "words")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WordJpaEntity extends BaseEntity {

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

    @Column(name = "message")
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

}
