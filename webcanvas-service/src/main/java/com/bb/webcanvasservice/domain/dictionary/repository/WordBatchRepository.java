package com.bb.webcanvasservice.domain.dictionary.repository;

import com.bb.webcanvasservice.domain.dictionary.entity.Word;

import java.util.List;

public interface WordBatchRepository {
    int saveInBatch(List<Word> words);
}
