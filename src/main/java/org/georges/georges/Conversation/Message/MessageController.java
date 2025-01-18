package org.georges.georges.Conversation.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.DTO.MessageDTO;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
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
    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/queue_name")
    public void setSession(@Payload Map<String, String> clientData) {
        String clientID = clientData.get("ClientID");
        String queueName = "testing";
        messagingTemplate.convertAndSend("/user/queue/" + clientID,queueName);
    }
    @MessageMapping("/chat/{queueID}")
    public void handleMessage(@DestinationVariable String queueID, @Payload MessageDTO messageDTO) {
        try {
            User sender = userRepository.findByUsername(messageDTO.getSender());
            User receiver = userRepository.findByUsername(messageDTO.getReceiver());
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


