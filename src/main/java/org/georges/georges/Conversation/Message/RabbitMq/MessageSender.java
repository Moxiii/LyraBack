package org.georges.georges.Conversation.Message.RabbitMq;

import org.georges.georges.Config.MessageSendEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private GenerateQueueName generateQueueName;

    public void sendDirectMessage(Long senderId, Long receiverId, String message) {
        String QUEUE_NAME = generateQueueName.privateQueueName(senderId, receiverId);
        String senderIdString = String.valueOf(senderId);
        String receiverIdString = String.valueOf(receiverId);
            String formattedMessage = String.format("{\"sender\": \"%s\", \"receiver\": \"%s\", \"content\": \"%s\"}",
                senderIdString, receiverIdString, message);
        rabbitTemplate.convertAndSend("private_message",QUEUE_NAME,formattedMessage);
        eventPublisher.publishEvent(new MessageSendEvent(message));
    }

}
