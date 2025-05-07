package com.bb.webcanvasservice.domain.dictionary;

import com.bb.webcanvasservice.common.sequence.SequenceRepository;
import com.bb.webcanvasservice.domain.dictionary.dto.ParseItem;
import com.bb.webcanvasservice.domain.dictionary.exception.DictionaryFileDownloadFailedException;
import com.bb.webcanvasservice.domain.dictionary.exception.DictionaryFileParseFailedException;
import com.bb.webcanvasservice.domain.dictionary.repository.WordRepository;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * https://opendict.korean.go.kr/main
 * 우리말 샘에서 제공하는 사전을 파싱해 DB에 적재하기 위한 클래스
 * TODO 배치 작업 처리 추가
 * TODO 형용사 어미 ~한 ~된 등으로 변경 로직 구상
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DictionaryService {

    private final ObjectMapper objectMapper;
    private final DictionaryProperties dictionaryProperties;

    private final WordRepository wordRepository;
    private final SequenceRepository sequenceRepository;
    private static final Pattern VALID_KOREAN = Pattern.compile("^[가-힣]+$");

    /**
     * 파일을 URL을 이용해 타겟 savePath에 파일을 다운로드한다.
     * @param fileURL
     * @param savePath
     */
    private void downloadFile(String fileURL, Path savePath) {
        try {
            URL url = new URL(fileURL);
            try (InputStream in = url.openStream()) {
                Files.createDirectories(savePath.getParent()); // 디렉터리 생성
                Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DictionaryFileDownloadFailedException();
        }
    }

    /**
     * 단일 파일 파싱 처리
     * @param index
     * @return
     */
    @Transactional
    public List<Word> parseSingleFile(int index) {
//            String targetUrl = dictionaryProperties.dataUrl() + "word_" + index + ".json";
        String targetUrl = "file:///Users/kanghyuk/Desktop/workspace/web-game-platform/words-json/word_" + index + ".json";
        Path downloadTempFilePath = null;
        try {
            downloadTempFilePath = Files.createTempFile("words_" + index, ".json");
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new DictionaryFileParseFailedException("임시 파일을 생성하는 도중 에러가 발생했습니다.");
        }


        List<Word> parsedWords = new ArrayList<>();
        Set<String> wordValues = new HashSet<>();

        try {
            log.debug("{}번 파일 다운로드 시작", index);
            downloadFile(targetUrl, downloadTempFilePath);
            log.debug("{}번 파일 다운로드 성공", index);

            final JsonFactory factory = objectMapper.getFactory();

            try (InputStream is = Files.newInputStream(downloadTempFilePath);
                 JsonParser parser = factory.createParser(is)) {

                // 1. JSON 시작
                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    if (parser.currentToken() != JsonToken.FIELD_NAME) {
                        continue;
                    }
                    String fieldName = parser.currentName();

                    // 2. channel 필드를 찾음
                    if ("channel".equals(fieldName)) {
                        parser.nextToken(); // channel 객체 안으로

                        while(parser.nextToken() != JsonToken.END_OBJECT) {
                            String channelField = parser.currentName();

                            // 3. item 배열을 찾는다.
                            if ("item".equals(channelField)) {
                                parser.nextToken(); // START_ARRAY 로 이동

                                while (parser.nextToken() != JsonToken.END_ARRAY && parser.currentToken() != null) {
                                    // 4. 배열 요소 하나를 DTO로 파싱

                                    ParseItem parseItem = objectMapper.readValue(parser, ParseItem.class);

                                    Word word = new Word(
                                            parseItem.wordinfo().word(),
                                            parseItem.senseinfo().cat_info() != null ? parseItem.senseinfo().cat_info().get(0).cat() : null,
                                            parseItem.wordinfo().word_type(),
                                            parseItem.senseinfo().type(),
                                            parseItem.wordinfo().word_unit(),
                                            parseItem.senseinfo().pos()
                                    );

                                    /**
                                     * 명사 / 형용사만 저장
                                     */
                                    if ("명사".equals(word.getPos())) {
                                        word.updateIndex(sequenceRepository.getNextValue("WORD_NOUN"));
                                    }
                                    else if ("형용사".equals(word.getPos())) {
                                        word.updateIndex(sequenceRepository.getNextValue("WORD_ADJECTIVE"));
                                    }
                                    else {
                                        continue;
                                    }

                                    /**
                                     * 이미 포함된 동음이의어는 추가로 저장하지 않음.
                                     */
                                    if (wordValues.contains(word.getValue())) {
                                        continue;
                                    }

                                    /**
                                     * 정규식 기반 필터링
                                     */
                                    if (!VALID_KOREAN.matcher(word.getValue()).matches()) {
                                        continue;
                                    }

                                    /**
                                     * word_type2가 일반어인 경우만 저장
                                     */
                                    if (!"일반어".equals(word.getType2())) {
                                        continue;
                                    }

                                    wordValues.add(word.getValue());
                                    parsedWords.add(word);

                                    /**
                                     * TODO
                                     * 파싱 로직 구현
                                     *
                                     * item.wordinfo.word_unit (어휘) -> Word.unit
                                     * item.wordinfo.word_type -> Word.type
                                     * item.wordinfo.word (target value)
                                     *
                                     * item.senseinfo.cat_info[0]
                                     * item.senseinfo.type (일반어)
                                     * item.senseinfo.pos (명사)
                                     */

                                }
                            }
                            else {
                                parser.skipChildren(); // item 필드 외의 필드는 무시한다.
                            }
                        }
                    }
                    else {
                        parser.skipChildren(); // channel 필드 외의 필드는 무시한다.
                    }
                }
            }
        } catch (DictionaryFileDownloadFailedException e) {
            // 다운로드 실패시까지 source 파일을 읽어온다.
            log.debug("{}번 파일 다운로드 요청 실패", index);
            throw e;
        } catch(Exception e) {
            log.debug("{}번 파일 파싱 실패", index);
            throw new DictionaryFileParseFailedException();
        }
        finally {
            try {
                if (downloadTempFilePath != null) {
                    Files.delete(downloadTempFilePath);
                    log.debug("{}번 파일 삭제 완료", index);
                }
            } catch (Exception e) {
                log.error("{}번 임시 파일 삭제 중 에러 발생", index);
                log.error(e.getMessage(), e);
            }
        }

        return parsedWords;
    }

    @Transactional
    public int parseFileAndSave(int index) {
        return wordRepository.saveInBatch(parseSingleFile(index));
    }

    /**
     * 순회하며 파일 파싱
     */
    @Transactional
    public void traverseFiles() {
        int index = 1;

        while (true) {
            try {
                List<Word> parsedWords = parseSingleFile(index++);
                wordRepository.saveInBatch(parsedWords);
            }
            catch(Exception e) {
                break;
            }
        }
    }
}
