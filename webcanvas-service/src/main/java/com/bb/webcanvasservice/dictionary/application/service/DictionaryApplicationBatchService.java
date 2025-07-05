package com.bb.webcanvasservice.dictionary.application.service;

import com.bb.webcanvasservice.common.lock.ConcurrencyLock;
import com.bb.webcanvasservice.infrastructure.lock.exception.LockAlreadyOccupiedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryApplicationBatchService {

    private final ConcurrencyLock concurrencyLock;
    private final DictionaryBatchJob dictionaryBatchJob;

    public void batchInsertWordDataWithLock() {
        String batchId = "word-data-initial-insert";

        try {
            concurrencyLock.executeAsyncWithLock(
                    batchId,
                    dictionaryBatchJob::batchInsertWordData
            );
        }
        catch(LockAlreadyOccupiedException e) {
            log.error(e.getMessage());
            log.error("이미 실행중인 배치입니다. ====== {}", batchId);
            throw new LockAlreadyOccupiedException("이미 실행중인 배치입니다.");
        }
    }
}
