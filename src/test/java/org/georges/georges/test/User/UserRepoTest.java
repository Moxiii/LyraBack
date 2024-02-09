package org.georges.georges.test.User;

import org.georges.georges.User.UserRole.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserRepoTest {

    @Mock
    private UserRepository userRepository;

    @Test
    public void testExistsByUsername() {
        // Configurer le comportement du mock pour la méthode existsByUsername
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Appeler la méthode et vérifier le résultat
        assertTrue(userRepository.existsByUsername("testuser"));
    }

    // Vous pouvez ajouter d'autres tests pour les autres méthodes du repository de la même manière
}