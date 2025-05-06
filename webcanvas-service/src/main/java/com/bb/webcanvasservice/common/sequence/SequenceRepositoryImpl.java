package com.bb.webcanvasservice.common.sequence;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sequence 사용을 위한 레포지토리
 */
@Slf4j
@RequiredArgsConstructor
@Repository
public class SequenceRepositoryImpl implements SequenceRepository {

    private final EntityManager entityManager;

    private final SequenceProperties sequenceProperties;

    private final String SEQUENCE_SELECT_QUERY = """
                select  s
                from    Sequence s
                where   s.name = :sequenceName
                """;


    @Transactional
    void setupSequences() {
        sequenceProperties.list().forEach(sequenceName -> {
            if (isExistSequence(sequenceName)) {
                return;
            }
            createSequence(sequenceName);
        });
    }


    /**
     * 시퀀스를 생성한다.
     * @param sequenceName
     * @return
     */
    @Override
    @Transactional
    public void createSequence(String sequenceName) {
        Boolean sequenceExists = isExistSequence(sequenceName);

        if (sequenceExists) {
            log.error("{} 이미 존재하는 시퀀스 명입니다.", sequenceName);
            throw new SequenceCreateFailedException();
        }

        Sequence sequence = new Sequence(sequenceName);
        entityManager.persist(sequence);
    }

    @Transactional(readOnly = true)
    public Boolean isExistSequence(String sequenceName) {
        return entityManager.createQuery("""
                        select  exists(
                          select  1
                          from    Sequence s
                          where   s.name = :sequenceName
                        )
                        
                        """, boolean.class)
                .setParameter("sequenceName", sequenceName)
                .getSingleResult();
    }

    /**
     * 시퀀스 값을 조회하고, 값을 하나 증가시킨다.
     * @param sequenceName
     * @return
     */
    @Override
    @Transactional
    public long getNextValue(String sequenceName) {
        Sequence sequence = null;

        try {
             sequence = entityManager.createQuery(SEQUENCE_SELECT_QUERY, Sequence.class)
                    .setParameter("sequenceName", sequenceName)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();
        }
        catch(Exception e) {
            log.error("{} 시퀀스를 찾는 중 에러가 발생했습니다.", sequenceName, e);
            throw new SequenceNotFoundException();
        }

        long nextValue = sequence.getValue() + 1;
        sequence.setValue(nextValue);
        return nextValue;
    }

}
