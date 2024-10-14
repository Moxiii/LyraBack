package org.georges.georges.Conversation.Message.RabbitMq;

@FunctionalInterface
public interface MessageHandler {
    void handleMessage(String messageContent);
}