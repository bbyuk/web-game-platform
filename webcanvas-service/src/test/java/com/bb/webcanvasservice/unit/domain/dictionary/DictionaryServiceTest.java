package com.bb.webcanvasservice.unit.domain.dictionary;

import com.bb.webcanvasservice.common.sequence.SequenceRepositoryImpl;
import com.bb.webcanvasservice.domain.dictionary.service.DictionaryService;
import com.bb.webcanvasservice.domain.dictionary.entity.Word;
import com.bb.webcanvasservice.domain.dictionary.enums.Language;
import com.bb.webcanvasservice.domain.dictionary.enums.PartOfSpeech;
import com.bb.webcanvasservice.domain.dictionary.repository.WordRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@DisplayName("[unit] [service] dictionary service 단위테스트")
class DictionaryServiceTest {

    @MockitoBean
    private WordRepository wordRepository;

    @MockitoBean
    private SequenceRepositoryImpl sequenceRepository;

    @Autowired
    private DictionaryService dictionaryService;

    @Test
    @DisplayName("랜덤 단어 뽑기 테스트")
    public void testDrawRandomWord() throws Exception {
        BDDMockito.given(sequenceRepository.getCurrentValue(any())).willReturn(2L);
        BDDMockito.given(wordRepository.findRandomWordByLanguageAndPos(any(), any())).willReturn(Optional.of(new Word(
                Language.KOREAN,
                "행복한",
                1234L,
                PartOfSpeech.ADJECTIVE
        )));

        // given
        String koreanAdjective = dictionaryService.drawRandomWordValue(Language.KOREAN, PartOfSpeech.ADJECTIVE);
        String koreanNickname = koreanAdjective + " 여우";

        // then
        Assertions.assertThat(koreanNickname).isEqualTo("행복한 여우");
    }
}