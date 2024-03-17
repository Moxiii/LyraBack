package org.georges.georges.Message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Message.RabbitMq.*;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRole.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
private RabbitTemplate rabbitTemplate;
@Autowired
MessageSender messageSender;
    @PostMapping("/sendPrivateMessage")
    public ResponseEntity<?> sendPrivateMessage(@RequestBody Message message , HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Supprimer le préfixe "Bearer "
            log.info("TOKEN AUTHORIZED");
            // Valider le jeton JWT
            log.info("JWTUTILS :{}" , jwtUtil);
            log.info("VALIDATE TOKEN : {}" , jwtUtil.validateToken(token));
            if (jwtUtil != null && jwtUtil.validateToken(token)) {
        try (Connection connection = RabbitmqConnection.getConnection();
             Channel channel = connection.createChannel()) {
            Long id = message.getId();
            String content = message.getContent();
            User sender = userRepository.findByUsername(message.getSender().getUsername());
            User receiver = userRepository.findByUsername(message.getReceiver().getUsername());
            log.info("Message id : {}" , id);
            log.info("Messages sender id :{}" , sender.getId());
            log.info("Message receiver id :{}" , receiver.getId());
            log.info("Message content : {}" , content);
            // Vérifier si l'utilisateur destinataire existe dans la base de données

            if (receiver == null) {
                // Si l'utilisateur destinataire n'existe pas, renvoyer une erreur
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Recipient user does not exist");
            }
                String queueName = new GenerateQueueName().privateQueueName(sender.getId(), receiver.getId());
            // Envoyer le message via RabbitMQ
            messageSender.sendDirectMessage(sender.getId(), receiver.getId() , message.getContent());
            //save message


            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer les erreurs de connexion RabbitMQ ou de base de données
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message");
        }
    }
        }
        log.info("NO TOKEN OR INVALID TOKEN");
        // Si le jeton n'est pas valide ou s'il est absent, renvoyer un code d'erreur
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Unauthorized"));
    }
@PostMapping("/sendGroupMessage")
@ResponseBody
public ResponseEntity<?> sendGroupMessage(@RequestBody Message message){
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Unauthorized"));
}
    @GetMapping("/messages")
    @ResponseBody
    public ResponseEntity<?> getAllMessages(HttpServletRequest request) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Supprimer le préfixe "Bearer "
            log.info("TOKEN AUTHORIZED");
            // Valider le jeton JWT
            log.info("JWTUTILS :{}" , jwtUtil);
            log.info("VALIDATE TOKEN : {}" , jwtUtil.validateToken(token));
            if (jwtUtil != null && jwtUtil.validateToken(token)) {
                // Extraire l'identifiant de l'utilisateur du jeton
                String username = jwtUtil.extractUsername(token);
                log.info("Lusername du token est : {}",username);
                // Utilisez l'identifiant de l'utilisateur pour récupérer les messages
                User currentUser = userRepository.findByUsername(username);
                List<Message> messages = messageRepository.findBySenderOrReceiver(currentUser , currentUser);

                // Générer la réponse avec les messages
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
        }
        log.info("NO TOKEN OR INVALID TOKEN");
        // Si le jeton n'est pas valide ou s'il est absent, renvoyer un code d'erreur
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Unauthorized"));
    }



    @PostMapping("/receivePrivateMessages")
    public ResponseEntity<?> receivePrivateMessages(@RequestBody Message message , HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Supprimer le préfixe "Bearer "
            if (jwtUtil != null && jwtUtil.validateToken(token)) {
                // Extraire l'identifiant de l'utilisateur du jeton
                String username = jwtUtil.extractUsername(token);
                log.info("Lusername du token est : {}", username);
                try {
                    // Définir le gestionnaire de messages pour enregistrer automatiquement les messages en base de données
                    MessageHandler messageHandler = new MessageHandler() {
                        @Override
                        public void handleMessage(String messageContent) {
                            try {
                                // Désérialiser le contenu du message en un objet Message
                                ObjectMapper mapper = new ObjectMapper();
                                Message message = mapper.readValue(messageContent, Message.class);
                                log.info("received message : {}" , mapper.readValue(messageContent, Message.class));

                                // Extraire les informations du message
                               // Long senderId = Long.parseLong(message.getSender().getId());
                               // Long receiverId = Long.parseLong(message.getReceiver().getId());
                                //String content = message.getContent();

                                // Enregistrer le message en base de données
                                // Assurez-vous que votre MessageRepository est injecté (autowired) dans cette classe
                                //messageRepository.save(new Message(senderId, receiverId, content));

                                log.info("Message enregistré en base de données avec succès.");
                            } catch (Exception e) {
                                log.error("Erreur lors de la sauvegarde du message en base de données : {}", e.getMessage());
                            }
                        }
                    };
                    User sender = userRepository.findByUsername(message.getSender().getUsername());
                    User receiver = userRepository.findByUsername(message.getReceiver().getUsername());
                    // Démarrer la réception des messages privés
                    Long senderId = sender.getId();
                    Long receiverId = receiver.getId();
                    MessageReceiver.receiveDirectMessages(senderId, receiverId, messageHandler);

                    return ResponseEntity.ok("Receiving private messages started successfully");
                } catch (Exception e) {
                    log.error("Failed to start receiving private messages: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start receiving private messages");
                }
            }

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Unauthorized"));
    }



}
