package org.georges.georges.Conversation.Message.RabbitMq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;

public class RabbitmqConnection {
    @Value("${rabbitmq.host}")
    static String host ;
    @Value("${rabbitmq.port}")
    static int port;
    @Value("${rabbitmq.username}")
    static String cred;

    public static Connection getConnection() throws Exception{

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPassword("guest");
        factory.setPort(5672);
        factory.setUsername("guest");
        return factory.newConnection();
    }
}
