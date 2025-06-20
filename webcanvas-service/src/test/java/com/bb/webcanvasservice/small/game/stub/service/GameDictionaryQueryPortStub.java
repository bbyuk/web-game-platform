package com.bb.webcanvasservice.small.game.stub.service;

import com.bb.webcanvasservice.game.application.port.dictionary.DictionaryQueryPort;

public class GameDictionaryQueryPortStub implements DictionaryQueryPort {
    @Override
    public String drawRandomKoreanNoun() {
        return "명사";
    }

    @Override
    public String drawRandomKoreanAdjective() {
        return "깔끔한";
    }
}
