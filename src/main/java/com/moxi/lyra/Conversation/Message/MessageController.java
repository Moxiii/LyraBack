package com.moxi.lyra.Conversation.Message;

import com.moxi.lyra.DTO.UserDTO;
import com.moxi.lyra.Mongo.Message.MessageMongoRepository;
import com.moxi.lyra.Mongo.Message.MongoMessage;
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

    @MessageMapping("/queue_name")
    public void setSession(@Payload Map<String, String> clientData) {
        String clientID = clientData.get("ClientID");
        String queueName = "testing";
        messagingTemplate.convertAndSend("/user/queue/" + clientID,queueName);
    }

@MessageMapping("/chat/{queueID}")
@SendToUser("/queue/messages/{queueID}")
public void handleMessage(@DestinationVariable String queueID, @Payload MessageDTO messageDTO) {
        UserDTO senderDTO = messageDTO.getSender();
        UserDTO receiverDTO = messageDTO.getReceiver();
        String sanitizedQueueID = queueID.replace("\"", "").replace("'", "");
        MessageDTO message = new MessageDTO();
        message.setSender(senderDTO);
        message.setReceiver(receiverDTO);
        message.setContent(messageDTO.getContent());
        MongoMessage mongoMessage = new MongoMessage(senderDTO.getUsername(), receiverDTO.getUsername(), message.getContent());
        messageService.saveMongoMessage(mongoMessage);
        messagingTemplate.convertAndSend("/user/"+receiverDTO.getUsername()+"/queue/messages/" + sanitizedQueueID, messageDTO);
}
}


