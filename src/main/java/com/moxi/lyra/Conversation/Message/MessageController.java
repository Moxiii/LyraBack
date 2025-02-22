package com.moxi.lyra.Conversation.Message;

import com.moxi.lyra.Conversation.Conversation;
import com.moxi.lyra.Conversation.ConversationService;
import com.moxi.lyra.DTO.UserDTO;
import com.moxi.lyra.Mongo.Message.MongoMessage;
import com.moxi.lyra.User.User;
import lombok.extern.slf4j.Slf4j;
import com.moxi.lyra.DTO.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("api/chat")
public class MessageController {


    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ConversationService conversationService;


@MessageMapping("/queue_name")
    public void setSession(@Payload Map<String, String> clientData) {
        String conversationID = clientData.get("ConversationID");
        String queueName = UUID.randomUUID().toString();
        Conversation conversation = conversationService.findById(Long.valueOf(conversationID));
        String conversationName = conversation.getName().isEmpty() ? conversation.getParticipants().toString() : conversation.getName();
    Map<String, String> response = new HashMap<>();
    response.put("queueName", queueName);
    response.put("conversationName", conversationName);

    for (User participant : conversation.getParticipants()) {
        messagingTemplate.convertAndSend("/user/queue/" + participant.getUsername(), response);
    }

    }

@MessageMapping("/chat/{queueID}/{conversationID}")
public void handleMessage(@DestinationVariable String queueID ,
                          @Payload MessageDTO messageDTO ,
                          @DestinationVariable String conversationID) {
        UserDTO senderDTO = messageDTO.getSender();
        String sanitizedQueueID = queueID.replace("\"", "").replace("'", "");

        Conversation conversation = conversationService.findById(Long.parseLong(conversationID));
        String conversationName = conversation.getName().isEmpty() ? conversation.getParticipants().toString() : conversation.getName();

        MessageDTO message = new MessageDTO();
        String destination;
        String receiverUsername;
        message.setSender(senderDTO);

        if(conversation.getParticipants().size() > 2 ){
            receiverUsername = sanitizedQueueID;
            destination = "/topic/"+conversationName + "/";
        }else{
             receiverUsername = conversation.getParticipants().stream()
                     .map(User::getUsername)
                     .filter(username -> !username.equals(senderDTO.getUsername()))
                    .findFirst().orElse(null);
            if (receiverUsername != null) {
                destination = "/user/" + receiverUsername + "/messages/";
            }else{ throw new RuntimeException("User not found"); }
        }
        message.setContent(messageDTO.getContent());
        MongoMessage mongoMessage = new MongoMessage(senderDTO.getUsername(),receiverUsername,  message.getContent());
        messageService.saveMongoMessage(mongoMessage);
        messagingTemplate.convertAndSend(destination + sanitizedQueueID, messageDTO);
}
}


