package com.bb.webcanvasservice.domain.dictionary;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 사전 관련 프로퍼티
 */
@ConfigurationProperties(prefix = "application.domain.dictionary")
public record DictionaryProperties(
        // source 데이터 URL
        String dataUrl
) {
}
