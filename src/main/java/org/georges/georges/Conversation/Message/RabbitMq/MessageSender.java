package org.georges.georges.Conversation.Message.RabbitMq;

import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.MessageSendEvent;
import org.georges.georges.Conversation.Message.MessageQueueService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class MessageSender {

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageQueueService messageQueueService;
    @Autowired
    private RabbitQueueService rabbitQueueService;
    public void sendDirectMessage(Long senderId, Long receiverId, String message) {
        String QUEUE_NAME = messageQueueService.getConversationId(senderId, receiverId);
        try {
            rabbitQueueService.addNewQueue(QUEUE_NAME , "private_message", QUEUE_NAME);
        }
        catch (Exception e){
            log.info("Failed to add queue");
            log.warn("Exception:{}" , e.getMessage());
        }
        String senderIdString = String.valueOf(senderId);
        String receiverIdString = String.valueOf(receiverId);
            String formattedMessage = String.format("{\"sender\": \"%s\", \"receiver\": \"%s\", \"content\": \"%s\"}",
                senderIdString, receiverIdString, message);
        rabbitTemplate.convertAndSend("private_message",QUEUE_NAME,formattedMessage);
        eventPublisher.publishEvent(new MessageSendEvent(message));
    }

}
