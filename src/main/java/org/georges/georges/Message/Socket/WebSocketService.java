package org.georges.georges.Message.Socket;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.georges.georges.Message.Message;
import org.georges.georges.Message.MessageRepository;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;

import java.util.Date;
import java.util.List;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(Long senderId, Long receiverId, String messageContent) {
        User sender = userRepository.findById(senderId).orElse(null);
        User receiver = userRepository.findById(receiverId).orElse(null);

        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("Invalid sender or receiver ID");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageContent);
        message.setTimestamp(new Date());

        messageRepository.save(message); // Sauvegarde du message dans la base de données

        try {
            String queueName = "messageQueue";
            rabbitTemplate.convertAndSend(queueName, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Message> getMessages(Long receiverId) {
        return messageRepository.findByReceiverIdOrderByTimestampAsc(receiverId);
    }
}
