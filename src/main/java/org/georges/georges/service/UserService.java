    package org.georges.georges.service;

    import org.georges.georges.pojos.User;
    import org.georges.georges.repository.UserRepository;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.stereotype.Service;

    import java.util.*;
    import java.util.stream.Collectors;


    @Service
    public class UserService {
        private final UserRepository userRepository;


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
    }
