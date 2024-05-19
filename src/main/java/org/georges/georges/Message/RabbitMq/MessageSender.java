package org.georges.georges.Message.RabbitMq;

import com.rabbitmq.client.*;
import org.georges.georges.Config.MessageSendEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

private RabbitMQConfig rabbitMQConfig = new RabbitMQConfig();
@Autowired
    private ApplicationEventPublisher eventPublisher;
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDirectMessage(Long senderId, Long receiverId, String message) {
        GenerateQueueName generateQueueName = new GenerateQueueName();
        String QUEUE_NAME = generateQueueName.privateQueueName(senderId, receiverId);
        String senderIdString = String.valueOf(senderId);
        String receiverIdString = String.valueOf(receiverId);
        String formattedMessage = String.format("{\"sender\": \"%s\", \"receiver\": \"%s\", \"content\": \"%s\"}",
                senderIdString, receiverIdString, message);
        rabbitTemplate.convertAndSend("private_message",QUEUE_NAME,formattedMessage);
        eventPublisher.publishEvent(new MessageSendEvent(message));
    }

}
