package com.bb.webcanvasservice.common.sequence.config;

import com.bb.webcanvasservice.common.sequence.SequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 시퀀스 초기화 설정
 */
@Configuration
@RequiredArgsConstructor
public class SequenceInitializeConfig {

    private final SequenceRepository sequenceRepository;
    private final SequenceProperties sequenceProperties;

    @Bean(initMethod = "initialize")
    SequenceInitializer sequenceInitializer() {
        return new SequenceInitializer(sequenceRepository, sequenceProperties);
    }

    static class SequenceInitializer {
        private final SequenceRepository repo;
        private final SequenceProperties props;

        SequenceInitializer(SequenceRepository repo, SequenceProperties props) {
            this.repo = repo;
            this.props = props;
        }

        public void initialize() {
            props.list().forEach(name -> {
                if (!repo.isExistSequence(name)) {
                    repo.createSequence(name);
                }
            });
        }
    }
}