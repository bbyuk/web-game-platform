package com.bb.webcanvasservice.domain.dictionary;

import com.bb.webcanvasservice.domain.dictionary.dto.ParseItem;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * https://opendict.korean.go.kr/main
 * 우리말 샘에서 제공하는 사전을 파싱해 DB에 적재하기 위한 클래스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OpenDictParser {

    private final ObjectMapper objectMapper;
    private final DictionaryProperties dictionaryProperties;
    private final WordRepository wordRepository;

    private void downloadFile(String fileURL, Path savePath) throws IOException {
        URL url = new URL(fileURL);
        try (InputStream in = url.openStream()) {
            Files.createDirectories(savePath.getParent()); // 디렉터리 생성
            Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }


    /**
     * 순회하며 파일 파싱
     */
    public void traverseFiles() {
        int index = 1;

        final JsonFactory factory = objectMapper.getFactory();

        while (true) {
            String targetUrl = dictionaryProperties.dataUrl() + "word_" + index + ".json";
            Path downloadTempFilePath = null;
            try {
                downloadTempFilePath = Files.createTempFile("words_" + index, ".json");
                log.debug("{}번 파일 다운로드 시작", index);
                downloadFile(targetUrl, downloadTempFilePath);
                log.debug("{}번 파일 다운로드 성공", index);

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
                            List<Word> wordsToSave = new ArrayList<>();

                            while(parser.nextToken() != JsonToken.END_OBJECT) {
                                String channelField = parser.currentName();

                                // 3. item 배열을 찾는다.
                                if ("item".equals(channelField)) {
                                    parser.nextToken(); // START_ARRAY 로 이동

                                    while (parser.nextToken() != JsonToken.END_ARRAY && parser.currentToken() != null) {
                                        // 4. 배열 요소 하나를 Map으로 파싱
//                                        Map<String, Object> item = objectMapper.readValue(parser, Map.class);

                                        ParseItem parseItem = objectMapper.readValue(parser, ParseItem.class);

                                        Word word = new Word(
                                                parseItem.wordinfo().word(),
                                                parseItem.senseinfo().cat_info() != null ? parseItem.senseinfo().cat_info().get(0).cat() : null,
                                                parseItem.wordinfo().word_type(),
                                                parseItem.senseinfo().type(),
                                                parseItem.wordinfo().word_unit(),
                                                parseItem.senseinfo().pos()
                                        );
                                        wordsToSave.add(word);

//                                        if (parseItem == null) {
//                                            log.debug("아이템이 없을수도 있나?");
//                                            continue;
//                                        }
//
//                                        if (parseItem.senseinfo() == null) {
//                                            log.debug("senseinfo가 없을수도 있나?");
//                                            continue;
//                                        }
//
//                                        if (parseItem.senseinfo().cat_info() == null) {
////                                            log.debug("{} = 카테고리 정보 없음", parseItem.wordinfo().word());
//                                            continue;
//                                        }
//                                        if (parseItem.senseinfo().cat_info().size() > 1) {
//                                            log.debug("{} = 복수 카테고리 보유",parseItem.wordinfo().word());
//                                            log.debug(parseItem.senseinfo().cat_info().stream().map(ParseItem.SenseInfo.Category::cat).collect(Collectors.joining(", ")));
//                                        }

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

//                                        LinkedHashMap<String, Object> wordinfo = (LinkedHashMap<String, Object>) item.get("wordinfo");
//                                        LinkedHashMap<String, Object> sensinfo = (LinkedHashMap<String, Object>) item.get("senseinfo");
//
//                                        System.out.println("sensinfo = " + sensinfo);
                                    }
                                    wordRepository.saveAll(wordsToSave);
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
                } catch (IOException e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }

                index++;
            } catch (IOException e) {
                // 다운로드 실패시까지 source 파일을 읽어온다.
                log.debug("{}번 파일 다운로드 요청 실패", index);
                log.debug(e.getMessage());
                break;
            } catch(IllegalStateException e) {
                log.debug("{}번 파일 파싱 실패", index);
                log.debug(e.getMessage());
                break;
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
        }

    }
}
