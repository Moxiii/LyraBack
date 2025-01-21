package com.moxi.lyra.Conversation.Message;

import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MessageQueueService {
    private final Queue<Message> temporaryQueue = new ConcurrentLinkedQueue<>();

    public void addMessageToQueue(Message message) {
        temporaryQueue.offer(message);
    }

    public Queue<Message> getMessageFromUser(String username) {
        return temporaryQueue;
    }
    public void clearQueue(String username) {
        temporaryQueue.clear();
    }
    public String getConversationId(Long senderID, Long recipientID){
        String conversationID = Math.min(recipientID, senderID)+ "" + Math.max(senderID, recipientID);
        return conversationID;
    }
}
