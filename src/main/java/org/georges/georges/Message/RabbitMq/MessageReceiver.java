package org.georges.georges.Message.RabbitMq;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageReceiver {
    public static final String EXCHANGE_NAME = "direct_messages";
    public static final DeliverCallback callback =  (consumerTag , delivery)->{
        String message = new String(delivery.getBody() , "UTF-8");
        log.info("Received message :{}" , message);
    };

    public static void receiveDirectMessages(Long senderId, Long receiverId, MessageHandler messageHandler) throws Exception {
        String QUEUE_NAME = GenerateQueueName.getInstance().privateQueueName(senderId,receiverId);

        Connection connection = RabbitmqConnection.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        channel.basicConsume(QUEUE_NAME, true, (consumerTag, delivery) -> {
            String messageContent = new String(delivery.getBody(), "UTF-8");
            messageHandler.handleMessage(messageContent);
        }, consumerTag -> {
            // handle cancellation
        });
    }

    public static void receiveGroupMessages(String queueName) throws Exception {
        try (Connection connection = RabbitmqConnection.getConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            String queue = channel.queueDeclare(queueName, false, false, false, null).getQueue();
            channel.queueBind(queue, EXCHANGE_NAME, "");
            channel.basicConsume(queue, true, callback, consumerTag -> {});
        }
    }
}