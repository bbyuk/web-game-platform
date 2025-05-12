package com.bb.webcanvasservice.domain.dictionary;

import com.bb.webcanvasservice.common.sequence.SequenceRepository;
import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import com.bb.webcanvasservice.domain.dictionary.exception.WordNotFoundException;
import com.bb.webcanvasservice.domain.dictionary.parser.DictionaryParser;
import com.bb.webcanvasservice.domain.dictionary.parser.elementary.ElementaryDictionaryParser;
import com.bb.webcanvasservice.domain.dictionary.repository.WordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.random.RandomGenerator;

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
    private final SequenceRepository sequenceRepository;

    public DictionaryService(ElementaryDictionaryParser dictionaryParser
            , WordRepository wordRepository
            , SequenceRepository sequenceRepository) {
        this.dictionaryParser = dictionaryParser;
        this.wordRepository = wordRepository;
        this.sequenceRepository = sequenceRepository;
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


    /**
     * 랜덤한 단어의 값을 조회해온다.
     * @param language 대상 언어
     * @param pos 품사
     * @return
     */
    @Transactional(readOnly = true)
    public String drawRandomWordValue(Language language, PartOfSpeech pos) {
        /**
         * 대상 단어 목록의 시퀀스를 조회해 index 범위를 구한다.
         * 1 <= index < sequenceValue
         */
        String sequenceName = language.name() + "_" + pos.name();
        long lowerBound = 1L;
        long upperBound = sequenceRepository.getCurrentValue(sequenceName);

        RandomGenerator randomGenerator = RandomGenerator.getDefault();
        long randomWordIndex = randomGenerator.nextLong(lowerBound, upperBound);

        return wordRepository.findByLanguageAndPosAndIndex(language, pos, randomWordIndex)
                .orElseThrow(WordNotFoundException::new)
                .getValue();
    }

}
