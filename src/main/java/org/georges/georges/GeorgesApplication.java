package org.georges.georges;

import org.georges.georges.Message.Message;
import org.georges.georges.Message.MessageRepository;
import org.georges.georges.User.UserRole.UserRole;
import org.georges.georges.User.UserRole.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRole.UserRepository;


@RestController
@SpringBootApplication
public class GeorgesApplication {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    private final MessageRepository messageRepository;
    public GeorgesApplication(UserRepository userRepository , UserRoleRepository userRoleRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.messageRepository = messageRepository;
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
            if(messageRepository.count()==0){
                User moxi = userRepository.findByUsername("moxi");
                User test = userRepository.findByUsername("test");

                // Créer des messages fictifs
                Message message1 = new Message(moxi, test, "Bonjour, comment ça va ?");
                Message message2 = new Message(test, moxi, "Salut ! Ça va bien, et toi ?");
                Message message3 = new Message(moxi, test, "Oui, ça va aussi. Que fais-tu de beau ?");
                // Ajouter d'autres messages fictifs selon vos besoins

                // Enregistrer les messages dans la base de données
                messageRepository.save(message1);
                messageRepository.save(message2);
                messageRepository.save(message3);
            }
        };
    }
    public static void main(String[] args) {
        SpringApplication.run(GeorgesApplication.class, args);
    }


}
