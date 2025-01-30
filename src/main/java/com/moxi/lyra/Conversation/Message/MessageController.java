package com.moxi.lyra.Conversation.Message;

import com.moxi.lyra.Conversation.Conversation;
import com.moxi.lyra.Conversation.ConversationService;
import com.moxi.lyra.DTO.ConversationDTO;
import com.moxi.lyra.DTO.UserDTO;
import com.moxi.lyra.Mongo.Message.MessageMongoRepository;
import com.moxi.lyra.Mongo.Message.MongoMessage;
import com.moxi.lyra.User.User;
import lombok.extern.slf4j.Slf4j;
import com.moxi.lyra.DTO.MessageDTO;
import com.moxi.lyra.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("api/chat")
public class MessageController {


    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
@Autowired
private ConversationService conversationService;
@Autowired
private MessageRepository messageRepository;

@MessageMapping("/queue_name")
    public void setSession(@Payload Map<String, String> clientData) {
        String clientID = clientData.get("ClientID");
        String queueName = "testing";
        messagingTemplate.convertAndSend("/user/queue/" + clientID,queueName);
    }

@MessageMapping("/chat/{queueID}/{conversationID}")
@SendToUser("/queue/messages/{queueID}")
public void handleMessage(@DestinationVariable String queueID ,
                          @Payload MessageDTO messageDTO ,
                          @DestinationVariable String conversationID) {
        UserDTO senderDTO = messageDTO.getSender();
        String sanitizedQueueID = queueID.replace("\"", "").replace("'", "");

        Conversation conversation = conversationService.findById(Long.parseLong(conversationID));


        MessageDTO message = new MessageDTO();
        String destination;
        String receiverUsername;
        message.setSender(senderDTO);

        if(conversation.getParticipants().size() > 2 ){
            receiverUsername = sanitizedQueueID;
            destination = "/topic/";
        }else{
             receiverUsername = conversation.getParticipants().stream()
                     .map(User::getUsername)
                     .filter(username -> !username.equals(senderDTO.getUsername()))
                    .findFirst().orElse(null);
             log.warn("RECEIVER USERNAME: " + receiverUsername);
            if (receiverUsername != null) {
                destination = "/user/" + receiverUsername + "/queue/messages/";
            }else{ throw new RuntimeException("User not found"); }
        }
        message.setContent(messageDTO.getContent());
        MongoMessage mongoMessage = new MongoMessage(senderDTO.getUsername(),receiverUsername,  message.getContent());
        messageService.saveMongoMessage(mongoMessage);
        messagingTemplate.convertAndSend(destination + sanitizedQueueID, messageDTO);
}
}


