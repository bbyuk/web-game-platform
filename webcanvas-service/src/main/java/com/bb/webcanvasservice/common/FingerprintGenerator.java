package com.bb.webcanvasservice.common;

import java.util.UUID;

/**
 * 새로 등록되는 유저의 Fingerprint를 생성
 */
public class FingerprintGenerator {

    private FingerprintGenerator() {}

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
