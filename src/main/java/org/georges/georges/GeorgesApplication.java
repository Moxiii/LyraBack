package org.georges.georges;

import org.georges.georges.pojos.UserRole;
import org.georges.georges.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.georges.georges.pojos.User;
import org.georges.georges.repository.UserRepository;


@RestController
@SpringBootApplication
public class GeorgesApplication {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public GeorgesApplication(UserRepository userRepository , UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Bean
    public CommandLineRunner defaultDataInitializer(){
        return args -> {
            if (userRepository.count()==0){
                UserRole user = new UserRole("user","user",1L);
                user= userRoleRepository.save(user);
                User moxi = new User("moxi","moxi","moxi@moxi.com","ee","10-10-2001",user);
                User test = new User("test","test","test@e.e","ee","10-10-2001",user);;
                userRepository.save(moxi);
                userRepository.save(test);
            }
        };
    }
    public static void main(String[] args) {
        SpringApplication.run(GeorgesApplication.class, args);
    }


}
