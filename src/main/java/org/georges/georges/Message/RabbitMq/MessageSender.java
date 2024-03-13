package org.georges.georges.Message.RabbitMq;

import com.rabbitmq.client.*;


public class MessageSender {


    public static void sendDirectMessage(String recipient, String message) throws Exception {
        String QUEUE_NAME= "privateChatQueue";
        try (Connection connection = RabbitmqConnection.getConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
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
