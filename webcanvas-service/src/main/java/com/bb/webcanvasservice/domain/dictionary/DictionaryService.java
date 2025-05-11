package com.bb.webcanvasservice.domain.dictionary;

import com.bb.webcanvasservice.domain.dictionary.parser.DictionaryParser;
import com.bb.webcanvasservice.domain.dictionary.parser.elementary.ElementaryDictionaryParser;
import com.bb.webcanvasservice.domain.dictionary.repository.WordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;

/**
 * https://opendict.korean.go.kr/main
 * 사전을 파싱해 DB에 적재하기 위해 제공하는 서비스
 * TODO 형용사 어미 ~한 ~된 등으로 변경 로직 구상
 */
@Slf4j
@Service
public class DictionaryService {

    private final DictionaryParser dictionaryParser;
    private final WordRepository wordRepository;

    public DictionaryService(ElementaryDictionaryParser dictionaryParser, WordRepository wordRepository) {
        this.dictionaryParser = dictionaryParser;
        this.wordRepository = wordRepository;
    }

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

}
