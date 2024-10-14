package org.georges.georges.Conversation.Message.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.georges.georges.Conversation.Message.Message;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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

        try {
            String formattedMessage = objectMapper.writeValueAsString(message);
            String queueName = "/queue/" + senderId;
            messagingTemplate.convertAndSend(queueName, formattedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

