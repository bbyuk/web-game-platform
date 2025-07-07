package com.bb.webcanvasservice.game.domain.port.external;

import com.bb.webcanvasservice.game.application.command.ProcessToNextTurnCommand;

public interface GameTurnTimerPort {
    /**
     * 게임 턴 타이머를 스케줄러에 등록한다.
     *
     * @param command 턴 진행 command
     * @param startDelaySeconds timer 등록시점부터 첫 콜백 실행까지 딜레이
     */
    void registerTurnTimer(ProcessToNextTurnCommand command, int startDelaySeconds);

    /**
     * 게임 턴 타이머를 중지한다.
     * @param gameSessionId 대상 세션 ID
     */
    void stopTurnTimer(Long gameSessionId);
}