package org.georges.georges.Message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.SecurityUtils;
import org.georges.georges.Conversation.RabbitMq.RabbitmqConnection;
import org.georges.georges.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.georges.georges.Conversation.RabbitMq.MessageReceiver.EXCHANGE_NAME;
@Slf4j
@RestController
public class MessageController {
@Autowired
private MessageRepository messageRepository;
    @PostMapping("/sendMessage")
    public void sendMessqge(@RequestBody Message message){
        try (Connection connection = RabbitmqConnection.getConnection();
             Channel channel = connection.createChannel()) {
            String content = message.getContent();
            String sender = message.getSender().getUsername();
            String recivier = message.getReceiver().getUsername();

            // Envoyer le message via RabbitMQ
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            channel.basicPublish(EXCHANGE_NAME, recivier, null, content.getBytes("UTF-8"));

            //todo Enregistrer le message dans la base de données
            messageRepository.save(message);
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer les erreurs de connexion RabbitMQ ou de base de données
            }
        }
    @GetMapping("/messages")
    @ResponseBody
    public List<Map<String, Object>> getAllMessages() {
        User currentUser = SecurityUtils.getCurrentUser();
        List<Message> messages;

        if (currentUser != null) {
            messages = messageRepository.findBySender(currentUser);
        } else {
            // Gérer le cas où aucun utilisateur n'est connecté
            messages = Collections.emptyList();
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (Message message : messages) {
            Map<String, Object> messageInfo = new HashMap<>();
            messageInfo.put("content", message.getContent());
            messageInfo.put("sender", message.getSender().getUsername());
            result.add(messageInfo);
        }

        return result;
    }

}
