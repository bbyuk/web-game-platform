package com.bb.webcanvasservice.domain.dictionary.parser;

import com.bb.webcanvasservice.common.sequence.SequenceRepository;
import com.bb.webcanvasservice.domain.dictionary.Word;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public abstract class DictionaryParser {

    protected final ObjectMapper objectMapper;
    protected final SequenceRepository sequenceRepository;
    protected static final Pattern VALID_KOREAN = Pattern.compile("^[가-힣]+$");

    public abstract List<Word> parse(Path path);
}
