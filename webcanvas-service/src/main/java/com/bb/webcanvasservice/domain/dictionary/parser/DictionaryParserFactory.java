package com.bb.webcanvasservice.domain.dictionary.parser;

import com.bb.webcanvasservice.domain.dictionary.DictionaryProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictionaryParserFactory {
    private final DictionaryProperties dictionaryProperties;
    private final ApplicationContext applicationContext;

    public DictionaryParser get() {
        return applicationContext.getBean(dictionaryProperties.source(), DictionaryParser.class);
    }
}
