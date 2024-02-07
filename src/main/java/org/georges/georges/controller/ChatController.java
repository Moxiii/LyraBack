package org.georges.georges.controller;

import org.georges.georges.pojos.ChatNotification;
import org.georges.georges.pojos.Message;
import org.georges.georges.service.ConversationService;
import org.georges.georges.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;



@Controller
public class ChatController {
    private final ConversationService conversationService;
    private final MessageService messageService;
    @Autowired
    private  SimpMessagingTemplate simpMessagingtemplate;

    public ChatController(ConversationService conversationService, MessageService messageService) {
        this.conversationService = conversationService;
        this.messageService = messageService;
    }
    @MessageMapping("/chat")
public void processMessage(
        @Payload Message message){
        Message savedMessage = messageService.saveMessage(message);
        if (savedMessage != null) {
        simpMessagingtemplate.convertAndSendToUser(message.getReceiverId().toString() , "queue/messages" , ChatNotification.builder()
                .id(savedMessage.getId())
                .senderId(savedMessage.getSender().getId())
                .receiverId(savedMessage.getReceiverId())
                .content(savedMessage.getContent())
                .build()
                );
        }
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public Message sendmessage(@Payload Message message){
        return message;
    }
    @MessageMapping("/addUser")
    @SendTo("/topic/public")
    public  Message adduser(@Payload Message message, SimpMessageHeaderAccessor  simpMessageHeaderAccessor){
simpMessageHeaderAccessor.getSessionAttributes().put("username" , message.getSender());
return message;
    }
@GetMapping("/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<Message>> findChatMessages(@PathVariable("SenderId") Long senderId, @PathVariable("receiverId") Long receiverId
){
    List<Message> messages = conversationService.findChatMessages(senderId, receiverId);
        return ResponseEntity.ok(messages);
}
}
