package org.georges.georges.Message.RabbitMq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitmqConnection {
    private static final String HOST = "localhost";
    public static Connection getConnection() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPassword("guest");
        factory.setPort(5672);
        factory.setUsername("guest");
        return factory.newConnection();
    }
}
