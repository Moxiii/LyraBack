package com.moxi.lyra.Conversation.Message.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.lyra.Conversation.Message.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class WebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentLinkedQueue<Message> sharedQueue = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<WebSocketSession> sessions = new ConcurrentLinkedQueue<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("New connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    public static void sendMessageToQueue(Message message) {
        sharedQueue.offer(message);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String payload = textMessage.getPayload();
        Message newMessage = objectMapper.readValue(payload, Message.class);

        sendMessageToQueue(newMessage);

        for (WebSocketSession client : sessions) {
            if (client.isOpen()) {
                client.sendMessage(new TextMessage(payload));
            }
        }
    }
}
