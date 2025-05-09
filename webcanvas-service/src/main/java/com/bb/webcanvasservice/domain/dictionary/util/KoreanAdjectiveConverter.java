package com.bb.webcanvasservice.domain.dictionary.util;

import java.util.*;

public class KoreanAdjectiveConverter {

    // 대표적인 ㅂ 불규칙 형용사
    private static final Set<String> IRREGULAR_B_ADJECTIVES = Set.of(
            "심, 어렵, 고맙", "아름답", "기쁨답", "슬픔답", "보드랍", "두렵", "가엾", "부드럽", "무섭", "반갑", "사랑스럽", "귀엽"
    );

    // ㄹ 탈락 불규칙: ㄹ로 끝나는 형용사 중 받침 ㄹ이 탈락하고 관형형 변화
    private static final Set<String> IRREGULAR_L_ADJECTIVES = Set.of(
            "길", "멀", "늘", "어릴", "다를", "어둡", "부드러울"
    );

    // ㅎ 탈락 불규칙
    private static final Set<String> IRREGULAR_H_ADJECTIVES = Set.of(
            "하얗", "빨갛", "노랗", "파랗", "까맣", "초록빛하얗"
    );

    // ㅡ 탈락 불규칙 (중간 모음 ㅡ 탈락)
    private static final Set<String> IRREGULAR_EU_ADJECTIVES = Set.of(
            "크", "뜨"
    );

    // ㅅ 불규칙 (예외적으로 ㅅ이 탈락하는 경우)
    private static final Set<String> IRREGULAR_S_ADJECTIVES = Set.of(
            "낫", "짓", "붓", "잇", "벗", "솟"
    );

    // 메인: 형용사 → 관형형 변환
    public static String toModifierForm(String adjective) {
        if (!adjective.endsWith("다")) return adjective;

        String stem = adjective.substring(0, adjective.length() - 1); // '다' 제거
        char lastChar = stem.charAt(stem.length() - 1);

        // ㅎ 불규칙
        for (String ir : IRREGULAR_H_ADJECTIVES) {
            if (stem.endsWith(ir)) {
                return stem.substring(0, stem.length() - 1) + "은";
            }
        }

        // ㄹ 불규칙
        for (String ir : IRREGULAR_L_ADJECTIVES) {
            if (stem.endsWith(ir)) {
                return stem.substring(0, stem.length() - 1) + "은";
            }
        }

        // ㅂ 불규칙
        for (String ir : IRREGULAR_B_ADJECTIVES) {
            if (stem.endsWith(ir)) {
                return stem.substring(0, stem.length() - 1) + "운";
            }
        }

        // ㅡ 불규칙
        for (String ir : IRREGULAR_EU_ADJECTIVES) {
            if (stem.endsWith(ir)) {
                return stem.substring(0, stem.length() - 1) + "은";
            }
        }

        // ㅅ 불규칙
        for (String ir : IRREGULAR_S_ADJECTIVES) {
            if (stem.endsWith(ir)) {
                return stem.substring(0, stem.length() - 1) + "은";
            }
        }

        // 일반 규칙 처리
        if (hasFinalConsonant(lastChar)) {
            return stem + "은"; // 받침 O
        } else {
            return mergeWithFinalConsonant(lastChar, 'ㄴ', stem); // 받침 X → 종성 ㄴ 결합
        }
    }

    // 받침 존재 여부 판단
    private static boolean hasFinalConsonant(char syllable) {
        int base = syllable - 0xAC00;
        return (base % 28) != 0;
    }

    // 종성 결합 (받침 없는 음절 + 종성 결합 → 완성형 음절 생성)
    private static String mergeWithFinalConsonant(char syllable, char jong, String stem) {
        int syllableIndex = syllable - 0xAC00;
        int cho = syllableIndex / (21 * 28);
        int jung = (syllableIndex % (21 * 28)) / 28;
        int jongIdx = getJongseongIndex(jong);
        if (jongIdx == -1) return stem + jong;
        char combined = (char) (0xAC00 + (cho * 21 * 28) + (jung * 28) + jongIdx);
        return stem.substring(0, stem.length() - 1) + combined;
    }

    // 종성 문자 → 인덱스
    private static int getJongseongIndex(char jong) {
        char[] jongseongs = {
                0x0000, 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ',
                'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ',
                'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ',
                'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        };

        for (int i = 0; i < jongseongs.length; i++) {
            if (jongseongs[i] == jong) return i;
        }
        return -1;
    }
}
