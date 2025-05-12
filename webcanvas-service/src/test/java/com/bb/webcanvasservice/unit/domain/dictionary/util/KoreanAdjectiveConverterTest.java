package com.bb.webcanvasservice.unit.domain.dictionary.util;

import com.bb.webcanvasservice.domain.dictionary.util.KoreanAdjectiveConverter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@DisplayName("[unit] [util]")
class KoreanAdjectiveConverterTest {

    @Test
    @DisplayName("-다 로 끝나는 형용사 변환")
    void testAdjectiveConvert() throws Exception {
        // given
        List<String> question = List.of(
                "억척스럽다",
                "하잘것없다",
                "아름답다",
                "기쁘다",
                "슬프다",
                "행복하다",
                "즐겁다",
                "밝다",
                "어둡다",
                "깨끗하다",
                "더럽다",
                "춥다",
                "덥다",
                "시원하다",
                "따뜻하다",
                "차갑다",
                "무겁다",
                "가볍다",
                "길다",
                "짧다",
                "높다",
                "낮다",
                "빠르다",
                "느리다",
                "부드럽다",
                "거칠다",
                "착하다",
                "나쁘다",
                "좋다",
                "귀엽다",
                "졸리다",
                "배고프다",
                "까맣다");
        List<String> answer = List.of(
                "억척스러운",
                "하잘것없는",
                "아름다운",
                "기쁜",
                "슬픈",
                "행복한",
                "즐거운",
                "밝은",
                "어두운",
                "깨끗한",
                "더러운",
                "추운",
                "더운",
                "시원한",
                "따뜻한",
                "차가운",
                "무거운",
                "가벼운",
                "긴",
                "짧은",
                "높은",
                "낮은",
                "빠른",
                "느린",
                "부드러운",
                "거친",
                "착한",
                "나쁜",
                "좋은",
                "귀여운",
                "졸린",
                "배고픈",
                "까만");

        // when
        List<String> converted = question.stream().map(KoreanAdjectiveConverter::toModifierForm).collect(Collectors.toList());

        // then
        Assertions.assertThat(answer).isEqualTo(converted);
    }
}