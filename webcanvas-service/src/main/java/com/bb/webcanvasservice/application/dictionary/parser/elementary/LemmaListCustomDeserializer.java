package com.bb.webcanvasservice.application.dictionary.parser.elementary;

import com.bb.webcanvasservice.domain.dictionary.exception.DictionaryFileParseFailedException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 단건, 다건 데이터가 융합되어 들어오는 데이터의 deserialize 커스터마이저
 */
public class LemmaListCustomDeserializer<T> extends JsonDeserializer<List<ElementaryParseItem.Lemma>> {

    @Override
    public List<ElementaryParseItem.Lemma> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = mapper.readTree(jsonParser);

        List<ElementaryParseItem.Lemma> data = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode item : node) {
                data.add(mapper.treeToValue(item, ElementaryParseItem.Lemma.class));
            }
        } else if (node.isObject()) {
            data.add(mapper.treeToValue(node, ElementaryParseItem.Lemma.class));
        } else {
            throw new DictionaryFileParseFailedException("사전 데이터를 파싱하는 도중에 문제가 발생했습니다.");
        }

        return data;
    }
}
