package com.bb.webcanvasservice.dictionary.application.parser.elementary;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * 한국어 기초사전 파싱 DTO
 * @param Lemma
 * @param feat
 */
public record ElementaryParseItem (
        @JsonDeserialize(using = LemmaListCustomDeserializer.class)
        List<Lemma> Lemma,
        @JsonDeserialize(using = FeatListCustomDeserializer.class)
        List<Feat> feat
) {
    public record Lemma(
            Feat feat
    ) {}

    public record Feat(
            String att,
            String val
    ) {}
}
