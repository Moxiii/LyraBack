    package org.georges.georges.service;

    import org.georges.georges.pojos.User;
    import org.georges.georges.repository.UserRepository;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.stereotype.Service;

    import java.util.ArrayList;
    import java.util.Date;
    import java.util.Hashtable;
    import java.util.List;
    import java.util.stream.Collectors;


    @Service
    public class UserService {
        private final UserRepository userRepository;


        public UserService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        public User findByUsername(String username) {
            User user = userRepository.findByPseudo(username);
                return user;
        }

        public void deleteUserByEmail(String Email){
            userRepository.deleteByEmail(Email);
        }
        public void  deleteUserById(Long userid){
             userRepository.deleteById(userid);
        }
    }
