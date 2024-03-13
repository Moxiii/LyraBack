package org.georges.georges.Message.RabbitMq;

public class GenerateQueueName {
    private static GenerateQueueName instance;

    public GenerateQueueName() {
    }
    public static GenerateQueueName getInstance() {
        if (instance == null) {
            instance = new GenerateQueueName();
        }
        return instance;
    }
    public String privateQueueName(Long senderId , Long receiverId){

    Long smallerId = Math.min(senderId , receiverId);
    Long largerId = Math.max(senderId,receiverId);

    return "private_"+smallerId+largerId;
    }
}
