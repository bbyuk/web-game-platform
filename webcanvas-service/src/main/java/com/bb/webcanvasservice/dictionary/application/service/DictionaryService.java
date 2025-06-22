package com.bb.webcanvasservice.dictionary.application.service;

import com.bb.webcanvasservice.dictionary.application.config.DictionarySourceProperties;
import com.bb.webcanvasservice.dictionary.application.parser.DictionaryParser;
import com.bb.webcanvasservice.dictionary.domain.exception.DictionaryFileParseFailedException;
import com.bb.webcanvasservice.dictionary.domain.exception.WordNotFoundException;
import com.bb.webcanvasservice.dictionary.domain.model.Language;
import com.bb.webcanvasservice.dictionary.domain.model.PartOfSpeech;
import com.bb.webcanvasservice.dictionary.domain.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.bb.webcanvasservice.dictionary.application.util.DictionaryDataFileUtils.*;

/**
 * 사전 Application 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final DictionarySourceProperties dictionarySourceProperties;
    private final DictionaryParser dictionaryParser;
    private final WordRepository wordRepository;



    /**
     * 파라미터로 주어진 파일 경로에 있는 파일을 파싱해 DB에 저장한다.
     * @param path
     * @return
     */
    @Transactional
    public int parseFileAndSave(Path path) {
        return wordRepository.saveInBatch(
                dictionaryParser.parse(path)
        );
    }

    /**
     * 단일 file 단위 transaction 적용으로 BatchService에는 Transactional 붙이지 않는다.
     *
     * @return
     */
    @Async("asyncBatchTaskExecutor")
    @Transactional
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
                    .forEach(path -> result.addAndGet(parseFileAndSave(path)));
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


    /**
     * 랜덤한 단어의 값을 조회해온다.
     * @param language 대상 언어
     * @param pos 품사
     * @return
     */
    @Transactional(readOnly = true)
    public String drawRandomWordValue(Language language, PartOfSpeech pos) {
        return wordRepository.findRandomWordByLanguageAndPos(language, pos)
                .orElseThrow(WordNotFoundException::new)
                .getValue();
    }
}
