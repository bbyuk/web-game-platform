package com.bb.webcanvasservice.dictionary.application.service;

import com.bb.webcanvasservice.dictionary.application.config.DictionarySourceProperties;
import com.bb.webcanvasservice.dictionary.domain.exception.DictionaryFileParseFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.bb.webcanvasservice.dictionary.application.util.DictionaryDataFileUtils.*;

/**
 * Dictionary 관련 배치 Job
 * 이후에 배치 작업이 늘어나고 세밀한 트랜잭션 관리가 필요한 경우 Spring Batch 도입을 고려
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryBatchJob {

    private final DictionarySourceProperties dictionarySourceProperties;
    private final DictionaryService dictionaryService;

    /**
     * 단일 file 단위 transaction 적용으로 BatchService에는 Transactional 붙이지 않는다.
     *
     * @return
     */
    @Async("asyncBatchTaskExecutor")
    public CompletableFuture<Integer> batchInsertWordData() {
        AtomicInteger result = new AtomicInteger(0);
        /**
         * TODO 파일 전체 다운로드 및 unzip 로직 추가
         * 개발중엔 임시로 프로젝트 루트 경로에 파일 다운로드 후 테스트
         */

        Path targetDirectory = null;
        Path downloadZipFilePath = null;

        if (dictionarySourceProperties.location().equals("local")) {
            targetDirectory = getLocalFilesDirectoryPath();
        } else {
            downloadZipFilePath = downloadFile(dictionarySourceProperties.dataUrl());
            targetDirectory = unzipFile(downloadZipFilePath);
        }

        try (Stream<Path> paths = Files.list(targetDirectory)) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> result.addAndGet(dictionaryService.parseFileAndSave(path)));
        } catch (IOException e) {
            log.error("데이터 파일 디렉터리 순회중 오류 발생", e);
            throw new DictionaryFileParseFailedException();
        }
        finally {
            if (dictionarySourceProperties.location().equals("download")) {
                try {
                    Files.delete(targetDirectory);
                    if (downloadZipFilePath != null) {
                        Files.delete(downloadZipFilePath);
                    }
                }
                catch (IOException e) {
                    log.error(e.getMessage(), e);
                    log.error("다운로드 받은 임시 파일을 삭제하는 과정에서 문제가 발생했습니다.");
                }
            }
        }

        log.debug("배치 수행 완료");
        return CompletableFuture.completedFuture(result.intValue());
    }

}
