package org.georges.georges.Conversation.Message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Config.SecurityUtils;
import org.georges.georges.Conversation.Message.RabbitMq.*;
import org.georges.georges.Conversation.Message.Socket.WebSocketService;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Slf4j
@RestController
@RequestMapping("api/chat")
public class MessageController {
@Autowired
private MessageRepository messageRepository;
@Autowired
private JwtUtil jwtUtil;
@Autowired
private UserRepository  userRepository;
    @Autowired
    private WebSocketService webSocketService;
@Autowired
MessageSender messageSender;
    @PostMapping("/sendPrivateMessage")
    public ResponseEntity<?> sendPrivateMessage(@RequestBody Message message , HttpServletRequest request){
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
        try (Connection connection = RabbitmqConnection.getConnection();
             Channel channel = connection.createChannel()) {
            Long id = message.getId();
            String content = message.getContent();
            User sender = userRepository.findByUsername(message.getSender().getUsername());
            User receiver = userRepository.findByUsername(message.getReceiver().getUsername());

            if (receiver == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Recipient user does not exist");
            }
                String queueName = new GenerateQueueName().privateQueueName(sender.getId(), receiver.getId());
            messageSender.sendDirectMessage(sender.getId(), receiver.getId() , message.getContent());
            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message");
        }
        }
        log.info("NO TOKEN OR INVALID TOKEN");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Unauthorized"));
    }

    @GetMapping("/messages")
    @ResponseBody
    public ResponseEntity<?> getAllMessages(HttpServletRequest request) throws Exception {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
                List<Message> messages = messageRepository.findBySenderOrReceiver(currentUser , currentUser);
                List<Map<String, Object>> result = new ArrayList<>();
                for (Message message : messages) {
                    Map<String, Object> messageInfo = new HashMap<>();
                    messageInfo.put("content", message.getContent());
                    messageInfo.put("sender", message.getSender().getUsername());
                    messageInfo.put("receiver" , message.getReceiver().getUsername());
                    result.add(messageInfo);
                }
                return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Unauthorized"));
    }

    @PostMapping("/receivePrivateMessages")
    public ResponseEntity<?> receivePrivateMessages(@RequestBody Message message , HttpServletRequest request) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
                try {
                    // Définir le gestionnaire de messages pour enregistrer automatiquement les messages en base de données
                    MessageHandler messageHandler = new MessageHandler() {
                        @Override
                        public void handleMessage(String messageContent) {
                            try {
                                // Désérialiser le contenu du message en un objet Message
                                ObjectMapper mapper = new ObjectMapper();
                                Message message = mapper.readValue(messageContent, Message.class);

                            } catch (Exception e) {
                                log.error("Erreur lors de la sauvegarde du message en base de données : {}", e.getMessage());
                            }
                        }
                    };
                    User sender = userRepository.findByUsername(message.getSender().getUsername());
                    User receiver = userRepository.findByUsername(message.getReceiver().getUsername());
                    Long senderId = sender.getId();
                    Long receiverId = receiver.getId();
                    MessageReceiver.receiveDirectMessages(senderId, receiverId, messageHandler);
                    return ResponseEntity.ok("Receiving private messages started successfully");
                } catch (Exception e) {
                    log.error("Failed to start receiving private messages: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start receiving private messages");
                }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Unauthorized"));
    }

    @PostMapping("/message/send")
    public ResponseEntity<?> sendMessage(@RequestParam Long senderId, @RequestParam Long receiverId, @RequestParam String content, HttpServletRequest request) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
                webSocketService.sendMessage(senderId, receiverId, content);
                return ResponseEntity.ok("Message sent successfully");
            }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Unauthorized"));
    }
}
