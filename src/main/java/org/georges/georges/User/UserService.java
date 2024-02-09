    package org.georges.georges.User;

    import org.georges.georges.User.UserRole.UserRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.util.*;


    @Service
    public class UserService {
        private final UserRepository userRepository;

@Autowired
private PasswordEncoder passwordEncoder;
        public UserService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        public User findByUsername(String username) {
            User user = userRepository.findByUsername(username);
                return user;
        }

        public void deleteUserByEmail(String Email){
            userRepository.deleteByEmail(Email);
        }
        public void  deleteUserById(Long userid){
             userRepository.deleteById(userid);
        }

        public List<User> searchParticipants(String query) {
               User userByName = userRepository.findByUsername(query);
            if (userByName != null) {
                return Collections.singletonList(userByName);
            }

            // Recherche par nom ou e-mail partiel
            List<User> usersByNameOrEmail = userRepository.findByUsernameContainingOrEmailContaining(query, query);
            return usersByNameOrEmail;
        }

        public User getUserById(Long id) {
            User user = userRepository.getById(id);
            return user;
        }

        public User createUser(User user) {
            // Encoder le mot de passe brut
            String encodedPassword = passwordEncoder.encode(user.getPassword());

            // Définir le mot de passe encodé sur l'utilisateur
            user.setPassword(encodedPassword);

            // Sauvegarder l'utilisateur dans la base de données
            return userRepository.save(user);
        }
    }
