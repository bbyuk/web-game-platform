package com.bb.webcanvasservice.websocket.security;

import com.bb.webcanvasservice.common.ban.Ban;
import com.bb.webcanvasservice.common.ban.BanRepository;
import com.bb.webcanvasservice.config.properties.WebSocketProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 비정상 접근 저장 및 밴 처리
 */
@Component
@RequiredArgsConstructor
public class AbnormalAccessRegistryImpl implements AbnormalAccessRegistry {

    private final WebSocketProperties webSocketProperties;
    private final Map<String, Integer> abnormalAccessMap = new ConcurrentHashMap<>();
    private final BanRepository banRepository;

    @Override
    public void add(String ipAddress) {
        abnormalAccessMap.putIfAbsent(ipAddress, 0);
        abnormalAccessMap.replace(ipAddress, abnormalAccessMap.get(ipAddress) + 1);

        if (abnormalAccessMap.get(ipAddress) == webSocketProperties.abnormalAccessLimit()) {
            banRepository.save(new Ban(ipAddress));
        }
    }
}
