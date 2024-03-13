package org.georges.georges.Message.RabbitMq;

@FunctionalInterface
public interface MessageHandler {
    void handleMessage(String messageContent);
}