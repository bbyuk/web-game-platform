package com.bb.webcanvasservice.infrastructure.persistence.dictionary;

import com.bb.webcanvasservice.domain.dictionary.model.Word;
import com.bb.webcanvasservice.infrastructure.persistence.dictionary.entity.WordJpaEntity;

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
