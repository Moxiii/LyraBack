package org.georges.georges.Message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Message.RabbitMq.MessageReceiver;
import org.georges.georges.Message.RabbitMq.MessageSender;
import org.georges.georges.Message.RabbitMq.RabbitmqConnection;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRole.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.georges.georges.Message.RabbitMq.MessageReceiver.EXCHANGE_NAME;
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

    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestBody Message message){
        try (Connection connection = RabbitmqConnection.getConnection();
             Channel channel = connection.createChannel()) {
            Long id = message.getId();
            String content = message.getContent();
            String sender = message.getSender().getUsername();
            String receiver = message.getReceiver().getUsername();
            log.info("Message id : {}" , id);
            log.info("Message receiver : {}" , receiver);
            log.info("Message sender : {}" , sender);
            log.info("Message content : {}" , content);
            // Vérifier si l'utilisateur destinataire existe dans la base de données
            User recipient = userRepository.findByUsername(receiver);
            if (recipient == null) {
                // Si l'utilisateur destinataire n'existe pas, renvoyer une erreur
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Recipient user does not exist");
            }

            // Envoyer le message via RabbitMQ
            MessageSender.sendDirectMessage(receiver,content);

            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer les erreurs de connexion RabbitMQ ou de base de données
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message");
        }
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
                List<Message> messages = messageRepository.findBySender(currentUser);

                // Générer la réponse avec les messages
                List<Map<String, Object>> result = new ArrayList<>();
                for (Message message : messages) {
                    Map<String, Object> messageInfo = new HashMap<>();
                    messageInfo.put("content", message.getContent());
                    messageInfo.put("sender", message.getSender().getUsername());
                    result.add(messageInfo);
                }
//                try {
//                    MessageReceiver.receiveDirectMessages((consumerTag, delivery) -> {
//                        String messageContent = new String(delivery.getBody(), "UTF-8");
//                        String senderUsername = delivery.getEnvelope().getRoutingKey();
//                        messages.add(new Message(senderUsername, currentUser.getUsername(), messageContent));
//                    });
//                } catch (Exception e) {
//                    log.error("Failed to receive direct messages: {}", e.getMessage());
//                }

                return ResponseEntity.ok(result);
            }
        }
        log.info("NO TOKEN OR INVALID TOKEN");
        // Si le jeton n'est pas valide ou s'il est absent, renvoyer un code d'erreur
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Unauthorized"));
    }

}
