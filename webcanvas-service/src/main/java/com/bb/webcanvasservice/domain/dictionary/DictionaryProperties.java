package com.bb.webcanvasservice.domain.dictionary;

import com.bb.webcanvasservice.domain.dictionary.parser.ParserInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * 사전 관련 프로퍼티
 */
@ConfigurationProperties(prefix = "application.domain.dictionary")
public record DictionaryProperties(
        String source,
        Map<String, ParserInfo> data
) {
}
