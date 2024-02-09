package org.georges.georges.test.User;

import org.georges.georges.User.User;
import org.georges.georges.User.UserRole.UserRepository;
import org.georges.georges.User.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testFindByUsername() {
        // Créer un utilisateur fictif pour le test
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Configurer le comportement du mock pour la méthode findByUsername
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        // Appeler la méthode et vérifier le résultat
        assertEquals("testuser", userService.findByUsername("testuser").getUsername());
    }

    // Ajouter d'autres tests pour les autres méthodes du service de la même manière
}
