package com.bb.webcanvasservice.unit.util;

import com.bb.webcanvasservice.domain.dictionary.OpenDictParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenDictParserTest {

    @Autowired
    private OpenDictParser openDictParser;

    @Test
    void parseTest() throws IOException {
        // given

        openDictParser.traverseFiles();

        // when

        // then

    }
}