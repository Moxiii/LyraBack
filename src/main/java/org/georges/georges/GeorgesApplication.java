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
                User martindrvt = new User("martin","martindvt","martin@martin.com","ee",dateString,user);
                moxi.setDescription("backend Dev of Gilbert");
                martindrvt.setDescription("CEO of the world");
                userRepository.save(moxi);
                userRepository.save(test);
                userRepository.save(martindrvt);
                userRepository.save(goerges);
            }
            if (todoRepository.count() == 0) {
                Task task1 = new Task("Description 1 " , "test" , false);
                Task task2 = new Task("Description 2 " , "test completed" , true);
                Task task3 = new Task("Description 3 " , "test " , false);
                Task task4 = new Task("Description 4 " , "test completed" , true);
                Todo todo = new Todo();
                Todo todo1 = new Todo();
                todo1.setTitle("Todo 1");
                todo.setTitle("Sample Todo");
                todo.setTask(Arrays.asList(task1, task2));
                todo1.setTask(Arrays.asList(task3,task4));
               User moxi = userRepository.findByUsername("moxi");
               todo1.setUser(moxi);
               todo.setUser(moxi);
                task1.setTodo(todo);
                task2.setTodo(todo);
                task3.setTodo(todo1);
                task4.setTodo(todo1);
                todoRepository.save(todo);
                todoRepository.save(todo1);
            }
        };
    }

}
