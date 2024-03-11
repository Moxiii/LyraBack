package org.georges.georges.Message.RabbitMq;
import com.rabbitmq.client.*;

public class MessageReceiver {
    public static final String EXCHANGE_NAME = "direct_messages";

    public static void receiveDirectMessages(String queueName, DeliverCallback callback) throws Exception {
        try (Connection connection = RabbitmqConnection.getConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            String queue = channel.queueDeclare(queueName, false, false, false, null).getQueue();
            channel.queueBind(queue, EXCHANGE_NAME, queueName);
            channel.basicConsume(queue, true, callback, consumerTag -> {});
        }
    }

    public static void receiveGroupMessages(String queueName, DeliverCallback callback) throws Exception {
        try (Connection connection = RabbitmqConnection.getConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            String queue = channel.queueDeclare(queueName, false, false, false, null).getQueue();
            channel.queueBind(queue, EXCHANGE_NAME, "");
            channel.basicConsume(queue, true, callback, consumerTag -> {});
        }
    }
}