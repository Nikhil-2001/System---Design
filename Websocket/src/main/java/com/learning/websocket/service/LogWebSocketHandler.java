package com.learning.websocket.service;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class LogWebSocketHandler extends TextWebSocketHandler {


    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }


    public void broadcastLine(String line) {
        TextMessage msg = new TextMessage(line);
        sessions.forEach(s -> {
            try {
                if (s.isOpen()) s.sendMessage(msg);
            } catch (IOException ignored) {
            }
        });
    }
}