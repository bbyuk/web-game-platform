package com.bb.webcanvasservice.domain.dictionary.dto;


import java.util.List;

public record ParseItem(
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
