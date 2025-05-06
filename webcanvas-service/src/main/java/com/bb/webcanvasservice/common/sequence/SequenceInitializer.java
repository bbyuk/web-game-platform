package com.bb.webcanvasservice.common.sequence;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SequenceInitializer {

    private final SequenceRepositoryImpl sequenceRepository;

    @PostConstruct
    public void setupSequences() {
        sequenceRepository.setupSequences();
    }
}
