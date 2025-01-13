package org.georges.georges.Conversation.Message.RabbitMq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GenerateQueueName {
@Autowired
private RabbitQueueService rabbitQueueService;

    public String privateQueueName(Long senderId , Long receiverId){

    Long smallerId = Math.min(senderId , receiverId);
    Long largerId = Math.max(senderId,receiverId);
    String QUEUE_NAME = "private_"+smallerId+largerId;
    try {
        rabbitQueueService.addNewQueue(QUEUE_NAME , "private_message", QUEUE_NAME);
    }
    catch (Exception e){
        log.info("Failed to add queue");
        log.warn("Exception:{}" , e.getMessage());
    }

    return QUEUE_NAME;
    }
}
