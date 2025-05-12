package com.bb.webcanvasservice.domain.dictionary.util;

import java.util.*;

public class KoreanAdjectiveConverter {

    // 공통으로 사용할 종성 배열
    private static final char[] JONGSEONGS = {
            '\0', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ',
            'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ',
            'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ',
            'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    /**
     * 받침이 있는지 확인
     * @param expect
     * @param value
     * @return
     */
    public static boolean hasSpecificFinalConsonant(char expect, char value) {
        Character jong = getFinalConsonant(value);
        return jong != null && jong.equals(expect);
    }

    /**
     * ㅡ 중성인지 확인
     * @param syllable
     * @return
     */
    public static boolean hasEuVowel(char syllable) {
        if (syllable < 0xAC00 || syllable > 0xD7A3) return false;

        int syllableIndex = syllable - 0xAC00;
        int jungIdx = (syllableIndex / 28) % 21;

        return jungIdx == 18;
    }

    // 메인: 형용사 → 관형형 변환
    public static String toModifierForm(String adjective) {
        if (!adjective.endsWith("다")) return adjective;

        String stem = adjective.substring(0, adjective.length() - 1); // '다' 제거
        char lastChar = stem.charAt(stem.length() - 1);
        String prefix = stem.substring(0, stem.length() - 1);

        // 받침이 있고
        if (hasFinalConsonant(lastChar)) {
            // 마지막 음절이 예외글자를 가진 경우
            if (hasSpecificFinalConsonant('ㅅ', lastChar)) {
                return prefix + removeFinalConsonant(lastChar) + '은';
            }
            if (hasSpecificFinalConsonant('ㅂ', lastChar)) {
                return prefix + removeFinalConsonant(lastChar) + '운';
            }
            if (hasSpecificFinalConsonant('ㄹ', lastChar)) {
                return prefix + removeFinalConsonant(lastChar) + '은';
            }
            if (hasSpecificFinalConsonant('ㅎ', lastChar)) {
                // ㅎ 대신 ㄴ으로 종성 변경
                return prefix + addFinalConstant(removeFinalConsonant(lastChar), 'ㄴ');
            }
            if (hasSpecificFinalConsonant('ㅄ', lastChar)) {
                return prefix + lastChar + '는';
            }

        }
        else if (hasEuVowel(lastChar)) {
            // ㅡ 로 끝남
            return prefix + addFinalConstant(lastChar, 'ㄴ');
        }

        // 일반 규칙 처리
        if (hasFinalConsonant(lastChar)) {
            return stem + "은"; // 받침 O
        } else {
            return prefix + addFinalConstant(lastChar, 'ㄴ'); // 받침 X → 종성 ㄴ 결합
        }
    }

    // 받침 존재 여부 판단
    private static boolean hasFinalConsonant(char syllable) {
        int base = syllable - 0xAC00;
        return (base % 28) != 0;
    }

    // 종성 결합 (받침 없는 음절 + 종성 결합 → 완성형 음절 생성)
    private static char addFinalConstant(char syllable, char jong) {
        int syllableIndex = syllable - 0xAC00;
        int cho = syllableIndex / (21 * 28);
        int jung = (syllableIndex % (21 * 28)) / 28;
        int jongIdx = getJongseongIndex(jong);
        if (jongIdx == -1) return syllable; // 유효하지 않으면 원래 음절 그대로 리턴
        return (char) (0xAC00 + (cho * 21 * 28) + (jung * 28) + jongIdx);
    }

    /**
     * 종성 인덱스 찾기
     * @param jong
     * @return
     */
    public static int getJongseongIndex(char jong) {
        for (int i = 0; i < JONGSEONGS.length; i++) {
            if (JONGSEONGS[i] == jong) return i;
        }
        return -1;
    }

    /**
     * 종성 찾기
     * @param syllable
     * @return
     */
    public static Character getFinalConsonant(char syllable) {
        if (syllable < 0xAC00 || syllable > 0xD7A3) return null;

        int syllableIndex = syllable - 0xAC00;
        int jongIndex = syllableIndex % 28;

        return jongIndex == 0 ? null : JONGSEONGS[jongIndex];
    }


    /**
     * 종성(받침) 제거
     * @param targetChar
     * @return
     */
    private static char removeFinalConsonant(char targetChar) {
        if (!hasFinalConsonant(targetChar)) {
            return targetChar; // 받침이 없다면 그대로 리턴
        }

        // 받침을 제거하고 새로 생성된 stem 리턴
        int syllableIndex = targetChar - 0xAC00;
        int choIndex = syllableIndex / (21 * 28);
        int jungIndex = (syllableIndex % (21 * 28)) / 28;

        // 받침 없애고, 새로운 음절로 만들어서 리턴
        return (char) (0xAC00 + (choIndex * 21 * 28) + (jungIndex * 28));
    }
}
