package org.georges.georges.Config;

import org.springframework.amqp.core.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue privateChatQueue() {
        
        return new Queue("privateChatQueue");
    }

    @Bean
    public Queue groupChatQueue() {
        return new Queue("groupChatQueue");
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("chat-exchange");
    }

    @Bean
    public Binding privateChatBinding(Queue privateChatQueue, TopicExchange exchange) {
        return BindingBuilder.bind(privateChatQueue).to(exchange).with("private.#");
    }

    @Bean
    public Binding groupChatBinding(Queue groupChatQueue, TopicExchange exchange) {
        return BindingBuilder.bind(groupChatQueue).to(exchange).with("group.#");
    }
}

