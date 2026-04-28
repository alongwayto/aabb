package cn.coderstory.springboot.device.ws;

import cn.coderstory.springboot.device.service.DeviceStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceStatusWebSocketHandler extends TextWebSocketHandler {

    private final DeviceStatusService deviceStatusService;
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.info("WebSocket connected: {}", session.getId());
        try {
            Map<String, Object> snapshot = deviceStatusService.getAllDeviceStatusSnapshot();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                    "type", "snapshot",
                    "data", snapshot
            ))));
        } catch (Exception e) {
            log.error("Failed to send snapshot", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        log.info("WebSocket disconnected: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.debug("WS message from {}: {}", session.getId(), message.getPayload());
    }

    public void broadcast(Object data) {
        String json;
        try {
            json = objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("Failed to serialize broadcast data", e);
            return;
        }
        for (String sessionId : new ArrayList<>(sessions.keySet())) {
            WebSocketSession session = sessions.get(sessionId);
            if (session == null) {
                continue;
            }
            if (!session.isOpen()) {
                sessions.remove(sessionId);
                continue;
            }
            try {
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                log.warn("Failed to send message to session {}", sessionId, e);
                sessions.remove(sessionId);
            }
        }
    }
}
