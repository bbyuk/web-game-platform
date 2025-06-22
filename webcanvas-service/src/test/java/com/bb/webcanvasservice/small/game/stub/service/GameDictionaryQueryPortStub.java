package com.bb.webcanvasservice.small.game.stub.service;

import com.bb.webcanvasservice.game.domain.port.dictionary.GameDictionaryQueryPort;

public class GameDictionaryQueryPortStub implements GameDictionaryQueryPort {
    @Override
    public String drawRandomKoreanNoun() {
        return "명사";
    }

    @Override
    public String drawRandomKoreanAdjective() {
        return "깔끔한";
    }
}
