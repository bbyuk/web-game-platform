package com.bb.webcanvasservice.domain.dictionary.batch;

import com.bb.webcanvasservice.common.lock.DistributedLock;
import com.bb.webcanvasservice.common.lock.LockAlreadyOccupiedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryBatchExecutor {

    private final DictionaryBatchJob dictionaryBatchJob;

    private final DistributedLock distributedLock;

    public void batchInsertWordDataWithLock() {
        String batchId = "word-data-initial-insert";

        try {
            distributedLock.executeAsyncWithLock(
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
