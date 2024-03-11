package org.georges.georges.Message.RabbitMq;

import com.rabbitmq.client.*;


public class MessageSender {
    private static final String EXCHANGE_NAME = "direct_messages";

    public static void sendDirectMessage(String recipient, String message) throws Exception {
        try (Connection connection = RabbitmqConnection.getConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            channel.basicPublish(EXCHANGE_NAME, recipient, null, message.getBytes("UTF-8"));
        }
    }

    public static void sendGroupMessage(String message) throws Exception {
        try (Connection connection = RabbitmqConnection.getConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
        }
    }
}
