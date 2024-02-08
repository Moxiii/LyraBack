package org.georges.georges.test.Message;

import org.georges.georges.pojos.Message;
import org.georges.georges.pojos.User;
import org.georges.georges.repository.MessageRepository;
import org.georges.georges.repository.UserRepository;
import org.georges.georges.service.MessageService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    public void testGetMessagesBetweenUsers() {
        // Créer des données de test
        Long senderId = 1L;
        Long receiverId = 2L;
        List<Message> expectedMessages = new ArrayList<>();
        // Ajouter des messages à la liste des messages attendus
        // ...

        // Mock du comportement du repository
        when(messageRepository.findBySenderIdAndReceiverIdOrderByTimestampAsc(senderId, receiverId)).thenReturn(expectedMessages);

        // Appeler la méthode du service
        List<Message> actualMessages = messageService.getMessagesBetweenUsers(senderId, receiverId);

        // Vérifier que la méthode du repository a été appelée avec les bons arguments
        verify(messageRepository).findBySenderIdAndReceiverIdOrderByTimestampAsc(senderId, receiverId);

        // Vérifier que les messages retournés par le service sont les mêmes que les messages attendus
        assertEquals(expectedMessages, actualMessages);
    }

    @Test
    public void testSaveMessage() {
        // Créer des données de test
        Message message = new Message();
        message.setContent("Test message");
        message.setSender(new User());
        message.setReceiver(new User());
        message.setTimestamp(new Date());

        // Mock du comportement du repository
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(messageRepository.save(any())).thenReturn(message);

        // Appeler la méthode du service
        Message savedMessage = messageService.saveMessage(message);

        // Vérifier que la méthode du repository a été appelée pour sauvegarder le message
        verify(messageRepository).save(message);

        // Vérifier que le message retourné par le service est le même que le message sauvegardé
        assertEquals(message, savedMessage);
    }

    // Ajouter d'autres tests pour les autres méthodes de service si nécessaire
}