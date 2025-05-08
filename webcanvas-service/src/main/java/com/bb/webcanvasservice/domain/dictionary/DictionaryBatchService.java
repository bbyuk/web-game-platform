package com.bb.webcanvasservice.domain.dictionary;

import com.bb.webcanvasservice.common.lock.DistributedLock;
import com.bb.webcanvasservice.domain.dictionary.exception.DictionaryFileDownloadFailedException;
import com.bb.webcanvasservice.domain.dictionary.exception.DictionaryFileParseFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryBatchService {
    private final DictionaryService dictionaryService;

    /**
     * 단일 file 단위 transaction 적용으로 BatchService에는 Transactional 붙이지 않는다.
     * @return
     */
    @Async("asyncBatchTaskExecutor")
    public CompletableFuture<Integer> batchInsertWordData() {
        AtomicInteger result = new AtomicInteger(0);
        /**
         * TODO 파일 전체 다운로드 및 unzip 로직 추가
         * 개발중엔 임시로 프로젝트 루트 경로에 파일 다운로드 후 테스트
         */

        Path classpathRoot = null;
        Path parentOfProjectRoot = null;
        try {
            classpathRoot = Paths.get(ClassLoader.getSystemResource("").toURI());
            parentOfProjectRoot = classpathRoot.getParent().getParent().getParent().getParent().getParent();
        } catch (URISyntaxException e) {
            log.error("파일 경로를 찾지 못했습니다.");
            throw new DictionaryFileDownloadFailedException();
        }
        Path targetDirectory = parentOfProjectRoot.resolve(Paths.get("words-json"));

        try (Stream<Path> paths = Files.list(targetDirectory)) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> result.addAndGet(dictionaryService.parseFileAndSave(path)));
        } catch (IOException e) {
            log.error("데이터 파일 디렉터리 순회중 오류 발생", e);
            throw new DictionaryFileParseFailedException();
        }

        log.debug("배치 수행 완료");
        return CompletableFuture.completedFuture(result.intValue());
    }

}
