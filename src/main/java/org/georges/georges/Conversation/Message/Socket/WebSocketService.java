package org.georges.georges.Conversation.Message.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.georges.georges.Conversation.Message.MessageRepository;
import org.georges.georges.Conversation.Message.Message;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        messageRepository.save(message);

        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
