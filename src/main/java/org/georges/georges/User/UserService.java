    package org.georges.georges.User;

    import org.springframework.stereotype.Service;

    import java.util.Collections;
    import java.util.List;


    @Service
    public class UserService {
        private final UserRepository userRepository;
        public UserService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }
        public User exists(String username) {
            username = username.toLowerCase();
            if(userRepository.existsByUsername(username)){
                return userRepository.findByUsername(username);
            }
            return null;
        }

        public User findByEmail(String email) {
            User user = userRepository.findByEmail(email);
            return user;
        }
        public void  deleteUserById(Long userid){
             userRepository.deleteById(userid);
        }

        public List<User> searchParticipants(String query) {
               User userByName = userRepository.findByUsername(query);
            if (userByName != null) {
                return Collections.singletonList(userByName);
            }

            List<User> usersByNameOrEmail = userRepository.findByUsernameContainingOrEmailContaining(query, query);
            return usersByNameOrEmail;
        }

        public User findByUsername(String username) {
            return userRepository.findByUsername(username);
        }
    }
