package com.bb.webcanvasservice.domain.dictionary.parser;

import com.bb.webcanvasservice.common.sequence.SequenceRepository;
import com.bb.webcanvasservice.domain.dictionary.Word;
import com.bb.webcanvasservice.domain.dictionary.dto.ParseItem;
import com.bb.webcanvasservice.domain.dictionary.util.KoreanAdjectiveConverter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 우리말샘의 사전을 파싱하는 파서
 * https://opendict.korean.go.kr/main
 */
@Slf4j
@Component
public class KoreanSamDictionaryParser extends DictionaryParser {

    public KoreanSamDictionaryParser(ObjectMapper objectMapper, SequenceRepository sequenceRepository) {
        super(objectMapper, sequenceRepository);
    }

    @Override
    public List<Word> parse(Path path) {
        JsonFactory factory = objectMapper.getFactory();
        List<Word> parsedWords = new ArrayList<>();
        Set<String> wordValues = new HashSet<>();

        try (InputStream is = Files.newInputStream(path);
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

                                /**
                                 * 명사 / 형용사만 저장
                                 */
                                String pos = parseItem.senseinfo().pos();
                                Long index = "명사".equals(pos)
                                        ? sequenceRepository.getNextValue("WORD_NOUN")
                                        : "형용사".equals(pos)
                                        ? sequenceRepository.getNextValue("WORD_ADJECTIVE")
                                        : -1;
                                if (index == -1) {
                                    continue;
                                }
                                String value = parseItem.wordinfo().word();

                                /**
                                 * 형용사일 경우 value converting 작업 수행
                                 */
                                if ("형용사".equals(pos)) {
                                    value = KoreanAdjectiveConverter.toModifierForm(value);
                                }


                                /**
                                 * 이미 포함된 동음이의어는 추가로 저장하지 않음.
                                 * 정규식 기반 필터링
                                 */
                                if (wordValues.contains(value) || !VALID_KOREAN.matcher(value).matches()) {
                                    continue;
                                }

                                String category = parseItem.senseinfo().cat_info() != null ? parseItem.senseinfo().cat_info().get(0).cat() : null;

                                /**
                                 * word_type2가 일반어인 경우만 저장
                                 */
                                String type1 = parseItem.wordinfo().word_type();
                                String type2 = parseItem.senseinfo().type();
                                if (!"일반어".equals(type2)) {
                                    continue;
                                }

                                /**
                                 * item.wordinfo.word_unit (어휘) -> Word.unit
                                 * item.wordinfo.word_type -> Word.type
                                 * item.wordinfo.word (target value)
                                 *
                                 * item.senseinfo.cat_info[0]
                                 * item.senseinfo.type (일반어)
                                 * item.senseinfo.pos (명사)
                                 */

                                Word word = new Word(
                                        value,
                                        index,
                                        category,
                                        type1,
                                        type2,
                                        parseItem.wordinfo().word_unit(),
                                        pos
                                );

                                /**
                                 * TODO 개발 및 테스트 후 삭제 필요
                                 */
                                if ("형용사".equals(word.getPos())) {
                                    word.setOriginalValue(parseItem.wordinfo().word());
                                }

                                wordValues.add(word.getValue());
                                parsedWords.add(word);
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
        catch(IOException e) {
            log.error("{} 파일 파싱 실패", path);
            log.error("파일을 파싱하는 도중 에러가 발생했습니다.", e);
        }
        return parsedWords;
    }
}
