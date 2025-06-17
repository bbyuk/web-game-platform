package com.bb.webcanvasservice.unit.common.sequence;

import com.bb.webcanvasservice.common.sequence.exception.SequenceCreateFailedException;
import com.bb.webcanvasservice.common.sequence.exception.SequenceNotFoundException;
import com.bb.webcanvasservice.common.sequence.config.SequenceProperties;
import com.bb.webcanvasservice.common.sequence.SequenceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@SpringBootTest
@DisplayName("[unit] [persistence] 시퀀스 처리 단위테스트")
class SequenceRepositoryImplTest {

    @Autowired
    private SequenceRepository sequenceRepository;

    @Autowired
    private SequenceProperties sequenceProperties;

    @Test
    @DisplayName("초기 시퀀스 자동생성 - 애플리케이션 실행시 property에 정의한 시퀀스들을 자동 생성한다.")
    void setupSequence() {
        // given
        sequenceProperties.list()
                .forEach(sequenceName -> {
                    Assertions.assertThatThrownBy(() -> sequenceRepository.createSequence(sequenceName))
                            .isInstanceOf(SequenceCreateFailedException.class);
                });

        // when

        // then

    }

    @Test
    @DisplayName("시퀀스 생성 - 시퀀스를 생성한다.")
    void createSequence() {
        // given
        String sequenceName = "test_sequence";

        // when
        sequenceRepository.createSequence(sequenceName);

        // then
    }

    @Test
    @DisplayName("시퀀스 생성 - 이미 생성이 되어있는 시퀀스명으로 생성 시도시 실패한다.")
    void createSequenceFailedWhenDuplicatedSequenceName() {
        // given
        String sequenceName = "test_sequence";
        String duplicatedSequenceName = "test_sequence";

        sequenceRepository.createSequence(sequenceName);

        // when
        Assertions.assertThatThrownBy(() -> sequenceRepository.createSequence(duplicatedSequenceName))
                .isInstanceOf(SequenceCreateFailedException.class);

        // then
    }

    @Test
    @DisplayName("시퀀스 getValue() - 시퀀스 명에 해당하는 시퀀스의 현재 값을 가져오고 다음 시퀀스 값으로 업데이트한다.")
    void getNextValue() {
        // given
        String sequenceName = "test_sequence";
        sequenceRepository.createSequence(sequenceName);

        // when
        long firstValue = sequenceRepository.getNextValue(sequenceName);
        long secondValue = sequenceRepository.getNextValue(sequenceName);

        // then
        Assertions.assertThat(secondValue - firstValue).isEqualTo(1);
    }

    @Test
    @DisplayName("시퀀스 getValue() - 시퀀스 명에 해당하는 시퀀스가 존재하지 않을 경우 nextValue 요청이 실패한다.")
    void getNextValueFailedWhenSequenceNotFound() {
        // given
        String sequenceName = "test_sequence";

        // when
        Assertions.assertThatThrownBy(() -> sequenceRepository.getNextValue(sequenceName))
                .isInstanceOf(SequenceNotFoundException.class);

        // then
    }

}