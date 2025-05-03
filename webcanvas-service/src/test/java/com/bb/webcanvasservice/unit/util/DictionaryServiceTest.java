package com.bb.webcanvasservice.unit.util;

import com.bb.webcanvasservice.domain.dictionary.DictionaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class DictionaryServiceTest {

    @Autowired
    private DictionaryService dictionaryService;

    @Test
    void parseTest() throws IOException {
        // given

        dictionaryService.traverseFiles();

        // when

        // then

    }
}