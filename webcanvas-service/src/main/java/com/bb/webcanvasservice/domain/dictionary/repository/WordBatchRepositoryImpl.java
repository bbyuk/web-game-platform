package com.bb.webcanvasservice.domain.dictionary.repository;

import com.bb.webcanvasservice.domain.dictionary.entity.Word;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WordBatchRepositoryImpl implements WordBatchRepository {

    private final EntityManager em;

    @Override
    @Transactional
    public int saveInBatch(List<Word> words) {
        int result = 0;
        for (int i = 0; i < words.size(); i++) {
            try {
                em.persist(words.get(i));
                result++;
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if (i % 1000 == 0) {
                em.flush();
                em.clear();
            }
        }
        return result;
    }
}
