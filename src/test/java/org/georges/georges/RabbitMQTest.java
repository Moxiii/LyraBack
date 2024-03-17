package org.georges.georges;

import org.georges.georges.Config.RabbitMQConfig;
import org.georges.georges.Message.RabbitMq.RabbitQueueService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

class RabbitMQTest {

    @MockBean
    private RabbitListenerEndpointRegistry endpointRegistry;

    @Mock
    private RabbitAdmin rabbitAdmin;

    @InjectMocks
    private RabbitQueueService rabbitQueueService;

    @Test
    void testAddNewQueue() {
        // Initialiser les mocks
        MockitoAnnotations.initMocks(this);

        // Données de test
        String queueName = "private_12";
        String exchangeName = "private_message";
        String routingKey = "private_12";

        // Exécuter la méthode à tester
        rabbitQueueService.addNewQueue(queueName, exchangeName, routingKey);

        // Vérifier que declareQueue a été appelé avec la bonne queue
        Queue expectedQueue = new Queue(queueName, true, false, false);
        verify(rabbitAdmin).declareQueue(expectedQueue);

        // Vérifier que declareBinding a été appelé avec le bon binding
        Binding expectedBinding = new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, null);
        verify(rabbitAdmin).declareBinding(expectedBinding);
    }
    @Test
    public void testCheckQueueExistOnListener() {
        RabbitQueueService rabbitQueueService = new RabbitQueueService();
        // Définir les valeurs de test
        String listenerId = "private_message";
        String queueName = "private_12";

        // Appeler la méthode et vérifier le résultat
        assertTrue(rabbitQueueService.checkQueueExistOnListener(listenerId, queueName));
    }
    @Test
    public void testGetMessageListenerContainerById() {
        RabbitQueueService rabbitQueueService = new RabbitQueueService();
        // Définir l'ID du conteneur de message à tester
        String listenerId = "private_message";

        // Appeler la méthode et vérifier que le conteneur de message retourné n'est pas null
        assertNotNull(rabbitQueueService.getMessageListenerContainerById(listenerId));
    }

}
