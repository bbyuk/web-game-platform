package com.bb.webcanvasservice.domain.dictionary.repository;

import com.bb.webcanvasservice.domain.dictionary.entity.Word;
import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class WordCustomRepositoryImpl implements WordCustomRepository {

    private final EntityManager em;

    /**
     * 품사 및 locale 언어를 기준으로 필터링 해 stream으로 메모리로 가져온다.
     * 이후 Iterator 기반 Reservoir sampling 방식으로 랜덤한 Entity 조회
     * @param language
     * @param pos
     * @return
     */
    @Override
    public Optional<Word> findRandomWordByLanguageAndPos(Language language, PartOfSpeech pos) {

        String jpql = """
                      select  w.id
                      from    Word w
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
                return Optional.of(em.find(Word.class, targetId));
            }
        }

        return Optional.empty();
    }
}
