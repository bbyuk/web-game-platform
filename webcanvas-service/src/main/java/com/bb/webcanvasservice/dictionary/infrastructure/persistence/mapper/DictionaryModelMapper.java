package com.bb.webcanvasservice.dictionary.infrastructure.persistence.mapper;

import com.bb.webcanvasservice.dictionary.domain.model.Word;
import com.bb.webcanvasservice.dictionary.infrastructure.persistence.entity.WordJpaEntity;

/**
 * domain model <-> Jpa Entity mapper
 * Dictionary word
 */
public class DictionaryModelMapper {

    /**
     * WordJpaEntity -> Word
     * @param entity WordJpaEntity
     * @return Word
     */
    public static Word toModel(WordJpaEntity entity) {
        return new Word(entity.getId(), entity.getLanguage(), entity.getValue(), entity.getIndex(), entity.getPos());
    }

    /**
     * Word -> WordJpaEntity
     * @param model Word
     * @return WordJpaEntity
     */
    public static WordJpaEntity toEntity(Word model) {
        return new WordJpaEntity(model.getId(), model.getLanguage(), model.getValue(), model.getIndex(), model.getPos());
    }
}
