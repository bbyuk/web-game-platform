package com.bb.webcanvasservice.application.dictionary.parser.koreansam;


import java.util.List;

/**
 * 한국어샘 사전데이터 파싱 DTO
 * @param wordinfo
 * @param senseinfo
 */
public record KoreanSamParseItem(
        WordInfo wordinfo,
        SenseInfo senseinfo
) {
    public record WordInfo(
            String word,
            String word_unit,
            String word_type
    ) {}
    public record SenseInfo(
            List<Category> cat_info,
            String type,
            String pos
    ) {
        public record Category(
                String cat
        ) {}
    }
}
