package org.georges.georges.controller;

import lombok.extern.slf4j.Slf4j;
import org.georges.georges.config.SecurityUtils;
import org.georges.georges.pojos.ChatNotification;
import org.georges.georges.pojos.Message;
import org.georges.georges.pojos.User;
import org.georges.georges.repository.UserRepository;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Controller
public class ChatController {
    private final ConversationService conversationService;
    private final MessageService messageService;
    private final UserRepository userRepository;
    @Autowired
    private  SimpMessagingTemplate simpMessagingtemplate;



    public ChatController(ConversationService conversationService, MessageService messageService , UserRepository userRepository) {
        this.conversationService = conversationService;
        this.messageService = messageService;
        this.userRepository =  userRepository;

    }
    @MessageMapping("/chat/process")
    public void processMessage(@Payload Map<String, String> messageData) {
        if (messageData == null || !messageData.containsKey("user_id") || !messageData.containsKey("receiver_id") || !messageData.containsKey("content")) {
            log.info("Le message est invalide : certaines données sont manquantes");
            // Gérer le cas où le message est invalide
            return;
        }

        // Extraire les données du message
        long userId = Long.parseLong(messageData.get("user_id"));
        long receiverId = Long.parseLong(messageData.get("receiver_id"));
        String content = messageData.get("content");

        // Recherchez les utilisateurs correspondant aux IDs
        Optional<User> optionalSender = userRepository.findById(userId);
        Optional<User> optionalReceiver = userRepository.findById(receiverId);

        if (optionalSender.isEmpty() || optionalReceiver.isEmpty()) {
            log.info("Le sender ou le receiver n'existe pas dans la base de données");
            // Gérer le cas où l'un des utilisateurs n'existe pas
            return;
        }

        // Créer un objet Message et l'enregistrer
        User sender = optionalSender.get();
        User receiver = optionalReceiver.get();
        Message message = new Message(sender, receiver, content);
        message.setTimestamp(new Date()); // Définir le timestamp

        // Enregistrez le message dans la base de données
        Message savedMessage = messageService.saveMessage(message);

        if (savedMessage != null) {
            simpMessagingtemplate.convertAndSendToUser(receiver.getId().toString(), "queue/messages", ChatNotification.builder()
                    .id(savedMessage.getId())
                    .senderId(savedMessage.getSender().getId())
                    .receiverId(savedMessage.getReceiver().getId())
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
