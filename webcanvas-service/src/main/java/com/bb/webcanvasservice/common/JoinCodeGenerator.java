package com.bb.webcanvasservice.common;

import java.security.SecureRandom;

/**
 * length 길이의 영문 대문자 + 숫자로 이루어진 게임 방 입장 코드를 생성
 */
public class JoinCodeGenerator {

    private JoinCodeGenerator() {}

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

}
