package com.bb.webcanvasservice.dictionary.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 사전 관련 프로퍼티
 */
@ConfigurationProperties(prefix = "application.domain.dictionary.source")
public record DictionarySourceProperties(
        String parser,
        String dataUrl,
        String location
) {
}
