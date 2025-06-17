package com.bb.webcanvasservice.dictionary.infrastructure.persistence.repository;

import com.bb.webcanvasservice.dictionary.domain.model.Language;
import com.bb.webcanvasservice.dictionary.domain.model.PartOfSpeech;
import com.bb.webcanvasservice.dictionary.domain.model.Word;
import com.bb.webcanvasservice.dictionary.application.repository.WordRepository;
import com.bb.webcanvasservice.dictionary.infrastructure.persistence.mapper.DictionaryModelMapper;
import com.bb.webcanvasservice.dictionary.infrastructure.persistence.entity.WordJpaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WordRepositoryImpl implements WordRepository {

    private final EntityManager em;

    @Override
    @Transactional
    public int saveInBatch(List<Word> words) {
        int result = 0;
        for (int i = 0; i < words.size(); i++) {
            try {
                em.persist(DictionaryModelMapper.toEntity(words.get(i)));
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

    /**
     * 품사 및 locale 언어를 기준으로 필터링 해 stream으로 메모리로 가져온다.
     * 이후 Iterator 기반 Reservoir sampling 방식으로 랜덤한 Entity 조회
     * @param language 언어
     * @param pos 품사
     * @return 랜덤 단어
     */
    @Override
    public Optional<Word> findRandomWordByLanguageAndPos(Language language, PartOfSpeech pos) {

        String jpql = """
                      select  w.id
                      from    WordJpaEntity w
                      where   w.pos = :pos
                      and     w.language = :language
                    """;

        try (Stream<Long> stream = em.createQuery(jpql, Long.class)
                .setParameter("pos", pos)
                .setParameter("language", language)
                .setHint("org.hibernate.annotations.QueryHints.READ_ONLY", true)
                .getResultStream()) {
            Iterator<Long> iterator = stream.iterator();

            int index = 0;
            Random random = ThreadLocalRandom.current();
            Long targetId = null;

            while (iterator.hasNext()) {
                Long current = iterator.next();
                if (random.nextInt(++index) == 0) {
                    targetId = current;
                }
            }

            if (targetId != null) {
                return Optional.of(DictionaryModelMapper.toModel(em.find(WordJpaEntity.class, targetId)));
            }
        }

        return Optional.empty();
    }
}
