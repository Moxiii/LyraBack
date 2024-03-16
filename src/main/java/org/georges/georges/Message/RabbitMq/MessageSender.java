package org.georges.georges.Message.RabbitMq;

import com.rabbitmq.client.*;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {


    public static void sendDirectMessage(Long senderId,Long receiverId, String message) throws Exception {
        GenerateQueueName generateQueueName = new GenerateQueueName();
       String QUEUE_NAME = generateQueueName.privateQueueName(senderId,receiverId);
        String senderIdString = String.valueOf(senderId);
        String receiverIdString = String.valueOf(receiverId);
        String formattedMessage = String.format("{\"sender\": \"%s\", \"receiver\": \"%s\", \"content\": \"%s\"}",
                senderIdString, receiverIdString, message);
        try (Connection connection = RabbitmqConnection.getConnection();

             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, formattedMessage.getBytes("UTF-8"));
        }
    }

    public static void sendGroupMessage(String message) throws Exception {
        String  QUEUE_NAME = "groupChatQueue";
        try (Connection connection = RabbitmqConnection.getConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        }
    }
}
