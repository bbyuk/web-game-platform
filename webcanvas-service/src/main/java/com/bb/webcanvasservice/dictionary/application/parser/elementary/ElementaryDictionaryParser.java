package com.bb.webcanvasservice.dictionary.application.parser.elementary;

import com.bb.webcanvasservice.dictionary.application.parser.DictionaryParser;
import com.bb.webcanvasservice.dictionary.application.util.KoreanAdjectiveConverter;
import com.bb.webcanvasservice.common.sequence.SequenceRepository;
import com.bb.webcanvasservice.dictionary.domain.exception.DictionaryFileParseFailedException;
import com.bb.webcanvasservice.dictionary.domain.model.Language;
import com.bb.webcanvasservice.dictionary.domain.model.PartOfSpeech;
import com.bb.webcanvasservice.dictionary.domain.model.Word;
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
 * 한국어 기초사전파서
 * https://krdict.korean.go.kr/kor/mainAction
 */
@Slf4j
@Component
public class ElementaryDictionaryParser extends DictionaryParser {

    public ElementaryDictionaryParser(ObjectMapper objectMapper, SequenceRepository sequenceRepository) {
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

                // 2. LexicalResource 필드를 찾음
                if ("LexicalResource".equals(fieldName)) {
                    parser.nextToken(); // LexicalResource 객체 안으로

                    while(parser.nextToken() != JsonToken.END_OBJECT) {
                        String lexicalResourceField = parser.currentName();

                        // 3. Lexicon 필드를 찾는다.
                        if ("Lexicon".equals(lexicalResourceField)) {
                            parser.nextToken(); // Lexicon 객체 안으로 로 이동
                            while(parser.nextToken() != JsonToken.END_OBJECT) {
                                String lexiconField = parser.currentName();

                                if ("LexicalEntry".equals(lexiconField)) {
                                    parser.nextToken(); // 배열 파싱

                                    while (parser.nextToken() != JsonToken.END_ARRAY && parser.currentToken() != null) {
                                        // 4. 배열 요소 하나를 DTO로 파싱

                                        ElementaryParseItem elementaryParseItem = objectMapper.readValue(parser, ElementaryParseItem.class);
                                        /**
                                         * 명사 / 형용사만 저장
                                         */
                                        String strPos = elementaryParseItem.feat().stream()
                                                .filter(feat -> "partOfSpeech".equals(feat.att()))
                                                .findFirst()
                                                .map(ElementaryParseItem.Feat::val)
                                                .orElseGet(() -> "없음");

                                        Long index = "명사".equals(strPos)
                                                ? sequenceRepository.getNextValue("KOREAN_NOUN")
                                                : "형용사".equals(strPos)
                                                ? sequenceRepository.getNextValue("KOREAN_ADJECTIVE")
                                                : -1;

                                        PartOfSpeech pos = strPos.equals("명사")
                                                ? PartOfSpeech.NOUN
                                                : strPos.equals("형용사")
                                                ? PartOfSpeech.ADJECTIVE
                                                : null;
                                        if (index == -1) {
                                            continue;
                                        }
                                        String originalValue = elementaryParseItem.Lemma()
                                                .stream().filter(Lemma -> "writtenForm".equals(Lemma.feat().att()))
                                                .findFirst()
                                                .map(Lemma -> Lemma.feat().val())
                                                .orElseThrow(() -> new DictionaryFileParseFailedException("값을 찾지 못했습니다."));
                                        String value = originalValue;
                                        /**
                                         * 형용사일 경우 message converting 작업 수행
                                         */
                                        if ("형용사".equals(strPos)) {
                                            value = KoreanAdjectiveConverter.toModifierForm(originalValue);
                                        }

                                        /**
                                         * 이미 포함된 동음이의어는 추가로 저장하지 않음.
                                         * 정규식 기반 필터링
                                         */
                                        if (wordValues.contains(value) || !VALID_KOREAN.matcher(value).matches()) {
                                            continue;
                                        }

                                        Word word = Word.create(
                                                Language.KOREAN,
                                                value,
                                                index,
                                                pos
                                        );

                                        wordValues.add(word.getValue());
                                        parsedWords.add(word);
                                    }
                                }
                                else {
                                    parser.skipChildren(); // LexicalEntry 필드 외의 필드는 무시한다.
                                }
                            }
                        }
                        else {
                            parser.skipChildren(); // Lexicon 필드 외의 필드는 무시한다.
                        }
                    }
                }
                else {
                    parser.skipChildren(); // LexicalResource 필드 외의 필드는 무시한다.
                }
            }
        }
        catch(IOException e) {
            log.error("{} 파일 파싱 실패", path);
            log.error("파일을 파싱하는 도중 에러가 발생했습니다.", e);
        }
        catch(Exception e) {
            log.error(e.getMessage(), e);
        }

        return parsedWords;
    }
}
