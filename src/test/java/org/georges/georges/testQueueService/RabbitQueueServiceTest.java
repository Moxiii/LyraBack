package org.georges.georges.testQueueService;

import org.georges.georges.Message.RabbitMq.RabbitQueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;

import static org.mockito.Mockito.*;


public class RabbitQueueServiceTest {

    @Mock
    private RabbitAdmin rabbitAdmin;

    @Mock
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @InjectMocks
    private RabbitQueueService rabbitQueueService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddNewQueue() {
        String queueName = "testQueue";
        String exchangeName = "private_message";
        String routingKey = "testRoutingKey";
        Queue queue = new Queue(queueName, true, false, false);
        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, null);

        rabbitQueueService.addNewQueue(queueName, exchangeName, routingKey);

        verify(rabbitAdmin, times(1)).declareQueue(queue);
        verify(rabbitAdmin, times(1)).declareBinding(binding);
        // Vérifie que la méthode addQueueToListener est appelée
        verify(rabbitQueueService, times(1)).addQueueToListener(eq(exchangeName), eq(queueName));
    }

    @Test
    public void testAddQueueToListener() {
        String listenerId = "testListener";
        String queueName = "testQueue";

        rabbitQueueService.addQueueToListener(listenerId, queueName);

        // Vérifie que la méthode getMessageListenerContainerById est appelée
        verify(rabbitQueueService, times(1)).getMessageListenerContainerById(listenerId);
        // Vérifie que la méthode addQueueNames est appelée sur le listener container
        verify(rabbitListenerEndpointRegistry, times(1)).getListenerContainer(listenerId);
    }

    // Écrivez d'autres tests pour les autres méthodes de RabbitQueueService...
}
