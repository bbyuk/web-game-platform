package com.bb.webcanvasservice.dictionary.infrastructure.config;

import com.bb.webcanvasservice.dictionary.domain.adapter.game.GameDictionaryQueryAdapter;
import com.bb.webcanvasservice.dictionary.domain.repository.WordRepository;
import com.bb.webcanvasservice.game.domain.port.dictionary.GameDictionaryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dictionary adapter 설정
 */
@Configuration
@RequiredArgsConstructor
public class DictionaryAdapterConfig {

    private final WordRepository wordRepository;

    /**
     * dictionary (upstream) -> game (downstream)
     * @return DictionaryQueryPort 구현체
     */
    @Bean
    public GameDictionaryQueryPort dictionaryQueryPort() {
        return new GameDictionaryQueryAdapter(wordRepository);
    }
}
