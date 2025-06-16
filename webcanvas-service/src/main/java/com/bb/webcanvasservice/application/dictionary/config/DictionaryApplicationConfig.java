package com.bb.webcanvasservice.application.dictionary.config;

import com.bb.webcanvasservice.domain.dictionary.repository.WordRepository;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dictionary application configuration class
 */
@Configuration
@RequiredArgsConstructor
public class DictionaryApplicationConfig {

    private final WordRepository wordRepository;

    @Bean
    public DictionaryService dictionaryService() {
        return new DictionaryService(wordRepository);
    }

}
