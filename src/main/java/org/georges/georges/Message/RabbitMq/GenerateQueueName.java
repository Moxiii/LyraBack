package org.georges.georges.Message.RabbitMq;

import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.SecurityUtils;
import org.georges.georges.User.User;
import org.georges.georges.User.UserQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
public class GenerateQueueName {
@Autowired
private RabbitQueueService rabbitQueueService;


    public List<String>  getAllQueueNames() {
        List<String> queueName = new ArrayList<>();
        User currentUser = SecurityUtils.getCurrentUser();
        if(currentUser != null ){
            List<UserQueue> userQueue = currentUser.getQueues();
            for (UserQueue queue : userQueue){
                queueName.add(queue.getName());
            }
        }
        return queueName;
    }

    public String privateQueueName(Long senderId , Long receiverId){

    Long smallerId = Math.min(senderId , receiverId);
    Long largerId = Math.max(senderId,receiverId);
    String QUEUE_NAME = "private_"+smallerId+largerId;
    try {
        RabbitQueueService rabbitQueueService1 = new RabbitQueueService();
        rabbitQueueService1.addNewQueue(QUEUE_NAME , null, null);
    }
    catch (Exception e){
        log.info("Failed to add queue");
        log.warn("Exception:{}" , e.getMessage());
    }

    return QUEUE_NAME;
    }
}
