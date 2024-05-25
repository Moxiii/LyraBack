package org.georges.georges;

import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Message.RabbitMq.RabbitMQConfig;
import org.georges.georges.Message.Message;
import org.georges.georges.Message.MessageRepository;
import org.georges.georges.Todo.Tasks.Task;
import org.georges.georges.Todo.Todo;
import org.georges.georges.Todo.TodoRepository;
import org.georges.georges.User.UserRole.UserRole;
import org.georges.georges.User.UserRole.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
@Slf4j
@SpringBootApplication
public class GeorgesApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeorgesApplication.class, args);
    }

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    private final MessageRepository messageRepository;
    private final TodoRepository todoRepository;

    public GeorgesApplication(UserRepository userRepository, UserRoleRepository userRoleRepository,
                              MessageRepository messageRepository, TodoRepository todoRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.messageRepository = messageRepository;
        this.todoRepository = todoRepository;
    }

    @Bean
    public CommandLineRunner defaultDataInitializer() {
        return args -> {
            Date aujourdhui = new Date();
            SimpleDateFormat formatedDate = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = formatedDate.format(aujourdhui);
            log.info("formated date" + dateString);
            if (userRepository.count()==0){
                UserRole user = new UserRole("user","user",1l);
                UserRole georgesRole = new UserRole("georges","georges",2l);
               user = userRoleRepository.save(user);
               georgesRole = userRoleRepository.save(georgesRole);
                User goerges = new User("georges" , "georges" , "georges.app.sav@gmail.com","ee",dateString , georgesRole);
                User moxi = new User("moxi","moxi","moxi@moxi.com","ee",dateString,user);
                User test = new User("test","test","test@e.e","ee",dateString,user);;
                User martindrvt = new User("martin","martindvt","test@e.e","ee",dateString,user);;
                userRepository.save(moxi);
                userRepository.save(test);
                userRepository.save(martindrvt);
                userRepository.save(goerges);
            }
            if (messageRepository.count() == 0) {
                User moxi = userRepository.findByUsername("moxi");
                User test = userRepository.findByUsername("test");
                Date curentTimeStamp = new Date();

                // Créer des messages fictifs
                Message message1 = new Message(moxi, test, "Bonjour, comment ça va ?");
                Message message2 = new Message(test, moxi, "Salut ! Ça va bien, et toi ?");
                Message message3 = new Message(moxi, test, "Oui, ça va aussi. Que fais-tu de beau ?");
                // Ajouter d'autres messages fictifs selon vos besoins
                message1.setTimestamp(curentTimeStamp);
                message2.setTimestamp(curentTimeStamp);
                message3.setTimestamp(curentTimeStamp);

                // Enregistrer les messages dans la base de données
                messageRepository.save(message1);
                messageRepository.save(message2);
                messageRepository.save(message3);
            }
            if (todoRepository.count() == 0) {
                Task task1 = new Task();
                task1.setDescription("Task 1 description");
                task1.setCompleted(false);
                Task task2 = new Task();
                task2.setDescription("Task 2 description");
                task2.setCompleted(true);
                Todo todo = new Todo();
                todo.setTitle("Sample Todo");
                todo.setTask(Arrays.asList(task1, task2));
               User moxi = userRepository.findByUsername("moxi");
                task1.setTodo(todo);
                task2.setTodo(todo);
                todoRepository.save(todo);
            }
        };
    }

}
