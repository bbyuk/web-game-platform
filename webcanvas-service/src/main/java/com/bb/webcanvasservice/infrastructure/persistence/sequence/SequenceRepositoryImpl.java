package com.bb.webcanvasservice.infrastructure.persistence.sequence;


import com.bb.webcanvasservice.common.sequence.SequenceRepository;
import com.bb.webcanvasservice.infrastructure.persistence.sequence.exception.SequenceCreateFailedException;
import com.bb.webcanvasservice.infrastructure.persistence.sequence.exception.SequenceNotFoundException;
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


    private final String SEQUENCE_SELECT_QUERY = """
                select  s
                from    SequenceJpaEntity s
                where   s.name = :sequenceName
                """;

    /**
     * 시퀀스를 생성한다.
     * @param sequenceName
     * @return
     */
    @Transactional
    @Override
    public void createSequence(String sequenceName) {
        Boolean sequenceExists = isExistSequence(sequenceName);

        if (sequenceExists) {
            log.error("{} 이미 존재하는 시퀀스 명입니다.", sequenceName);
            throw new SequenceCreateFailedException();
        }

        SequenceJpaEntity sequenceJpaEntity = new SequenceJpaEntity(sequenceName);
        entityManager.persist(sequenceJpaEntity);
    }

    /**
     * 시퀀스 값을 조회하고, 값을 하나 증가시킨다.
     * @param sequenceName
     * @return
     */
    @Transactional
    @Override
    public long getNextValue(String sequenceName) {
        SequenceJpaEntity sequenceJpaEntity = null;

        try {
             sequenceJpaEntity = entityManager.createQuery(SEQUENCE_SELECT_QUERY, SequenceJpaEntity.class)
                    .setParameter("sequenceName", sequenceName)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();
        }
        catch(Exception e) {
            log.error("{} 시퀀스를 찾는 중 에러가 발생했습니다.", sequenceName, e);
            throw new SequenceNotFoundException();
        }

        long nextValue = sequenceJpaEntity.getValue() + 1;
        sequenceJpaEntity.setValue(nextValue);
        return nextValue;
    }

    /**
     * 대상 시퀀스의 현재 value를 조회한다.
     * @param sequenceName
     * @return
     */
    @Transactional
    @Override
    public long getCurrentValue(String sequenceName) {
        SequenceJpaEntity sequenceJpaEntity = null;

        try {
            sequenceJpaEntity = entityManager.createQuery(SEQUENCE_SELECT_QUERY, SequenceJpaEntity.class)
                    .setParameter("sequenceName", sequenceName)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();
        }
        catch(Exception e) {
            log.error("{} 시퀀스를 찾는 중 에러가 발생했습니다.", sequenceName, e);
            throw new SequenceNotFoundException();
        }

        return sequenceJpaEntity.getValue();
    }


    /**
     * 시퀀스가 존재하는지 여부를 조회한다.
     * @param sequenceName
     * @return
     */
    @Transactional(readOnly = true)
    @Override
    public boolean isExistSequence(String sequenceName) {
        return entityManager.createQuery("""
                        select  exists(
                          select  1
                          from    SequenceJpaEntity s
                          where   s.name = :sequenceName
                        )
                        
                        """, boolean.class)
                .setParameter("sequenceName", sequenceName)
                .getSingleResult();
    }

}
