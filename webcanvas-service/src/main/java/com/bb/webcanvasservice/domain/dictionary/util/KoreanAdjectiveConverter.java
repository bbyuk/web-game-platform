package com.bb.webcanvasservice.domain.dictionary.util;

public class KoreanAdjectiveConverter {

    public static String toModifierForm(String adjective) {
        if (!adjective.endsWith("다")) return adjective;

        String stem = adjective.substring(0, adjective.length() - 1); // remove '다'

        // ㅂ 불규칙 처리
        if (stem.endsWith("답")) return stem.substring(0, stem.length() - 1) + "운"; // 아름답다 → 아름다운
        if (stem.endsWith("럽")) return stem.substring(0, stem.length() - 1) + "운"; // 사랑스럽다 → 사랑스러운

        // 하다형 형용사
        if (stem.endsWith("하")) return stem + "ㄴ"; // 깨끗하다 → 깨끗한

        // 기본형 처리
        char lastChar = stem.charAt(stem.length() - 1);
        if (hasFinalConsonant(lastChar)) {
            return stem + "은"; // 받침 있음 → 은
        } else {
            return stem + "ㄴ"; // 받침 없음 → ㄴ
        }
    }

    // 한글 종성(받침) 존재 여부 확인
    private static boolean hasFinalConsonant(char ch) {
        int baseCode = ch - 0xAC00;
        return baseCode >= 0 && (baseCode % 28 != 0);
    }
}
