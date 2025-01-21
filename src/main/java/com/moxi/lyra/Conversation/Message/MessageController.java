package com.moxi.lyra.Conversation.Message;

import lombok.extern.slf4j.Slf4j;
import com.moxi.lyra.DTO.MessageDTO;
import com.moxi.lyra.User.User;
import com.moxi.lyra.User.UserRepository;
import com.moxi.lyra.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("api/chat")
public class MessageController {


    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private UserService userService;

    @MessageMapping("/queue_name")
    public void setSession(@Payload Map<String, String> clientData) {
        String clientID = clientData.get("ClientID");
        String queueName = "testing";
        messagingTemplate.convertAndSend("/user/queue/" + clientID,queueName);
    }
    @MessageMapping("/chat/{queueID}")
    public void handleMessage(@DestinationVariable String queueID, @Payload MessageDTO messageDTO) {
        try {
            User sender = userService.findByUsername(messageDTO.getSender());
            User receiver = userService.findByUsername(messageDTO.getReceiver());
            Message message = new Message(sender ,receiver , messageDTO.getContent());
            log.info("Message reçu sur la queue '{}'", queueID);
            log.info("Contenu du message: {}", message.getContent());
            log.info("Expéditeur: {}", message.getSender());
            log.info("Destinataire: {}", message.getReceiver());
            log.info("Timestamp: {}", message.getTimestamp());

            messagingTemplate.convertAndSend("/queue/messages/" + queueID, message);

        } catch (Exception e) {
            log.error("Erreur de désérialisation du message: ", e);
        }
    };
    }


