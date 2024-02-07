package org.georges.georges.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.pojos.Message;
import org.georges.georges.pojos.Status;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username =  (String) headerAccessor.getSessionAttributes().get("username");
        if (username !=null){
            log.info("User disconnected : {}" + username);
            var chatMessage = Message.builder()
                    .status(Status.Offline)
                    .build();
        }
    }
}
