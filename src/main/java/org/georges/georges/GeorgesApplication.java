package org.georges.georges;

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

import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class GeorgesApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeorgesApplication.class, args);
    }

    RabbitMQConfig rabbitMQConfig = new RabbitMQConfig();
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
            if (userRepository.count()==0){
                UserRole user = new UserRole("user","user",1L);
                user= userRoleRepository.save(user);
                User moxi = new User("moxi","moxi","moxi@moxi.com","ee","10-10-2001",user);
                User test = new User("test","test","test@e.e","ee","10-10-2001",user);;
                User martindrvt = new User("martin","martindvt","test@e.e","ee","10-10-2001",user);;
                userRepository.save(moxi);
                userRepository.save(test);
                userRepository.save(martindrvt);
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

                task1.setTodo(todo);
                task2.setTodo(todo);

                todoRepository.save(todo);
            }
        };
    }

}
