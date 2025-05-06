package com.bb.webcanvasservice.common.sequence;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface SequenceRepository {
    long getNextValue(@Param("sequenceName") String sequenceName);
    void createSequence(@Param("sequenceName") String sequenceName);
}
