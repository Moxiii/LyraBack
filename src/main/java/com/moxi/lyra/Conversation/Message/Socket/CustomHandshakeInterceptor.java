package com.moxi.lyra.Conversation.Message.Socket;

import lombok.extern.slf4j.Slf4j;
import com.moxi.lyra.Config.Utils.JwtUtil;
import com.moxi.lyra.Conversation.Message.MessageQueueService;
import com.moxi.lyra.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@Slf4j
@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageQueueService queue;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, org.springframework.web.socket.WebSocketHandler wsHandler, Exception exception) {

    }
}