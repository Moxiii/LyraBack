package org.georges.georges.Conversation.Message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("api/chat")
public class MessageController {


    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @MessageMapping("/queue_name")
    public void setSession(@Payload Map<String, String> clientData) {
        String clientID = clientData.get("ClientID");
        String queueName = "testing";
        messagingTemplate.convertAndSend("/user/queue/" + clientID,queueName);
    }
    @MessageMapping("/chat/{queueID}")
    public void handleMessage(@DestinationVariable String queueID, @Payload Map<String , String>  message) {
        log.info("Message envoyé à la queue : {} avec message : {}", queueID, message);
        messagingTemplate.convertAndSend( "/queue/messages/"+queueID, message);
    };
    }


