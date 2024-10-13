package org.georges.georges.Todo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Config.SecurityUtils;
import org.georges.georges.Response.TodoRes;
import org.georges.georges.Todo.Tasks.Task;
import org.georges.georges.Todo.Tasks.TaskRepository;
import org.georges.georges.Todo.Tasks.TaskService;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/todo")
public class TodoController {
    @Autowired
    private TodoService todoService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
@Autowired
private TaskRepository taskRepository;
    @GetMapping("/")
    public ResponseEntity<?> getAllTodosWithTasks(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            log.info("TOKEN AUTHORIZED");
            if (jwtUtil != null && jwtUtil.validateToken(token)) {
                List<Todo> todos = todoRepository.findAll();
                for (Todo todo : todos) {
                    List<Task> tasks = taskService.getAllTasksByTodoId(todo.getId());
                    todo.setTask(tasks);
                }
                return ResponseEntity.ok(todos);
            }
        }
        log.info("NO TOKEN OR INVALID TOKEN");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }
@GetMapping("/{todoid}")
public ResponseEntity<?> getTodoByID(HttpServletRequest request , @PathVariable Long todoid){
    String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.substring(7);
        log.info("TOKEN AUTHORIZED");
        if (jwtUtil != null && jwtUtil.validateToken(token)) {
            Todo todo = todoRepository.findById(todoid).orElseThrow(() -> new EntityNotFoundException("Todo not found with id : " + todoid));
            return ResponseEntity.ok().body(todo);

        }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
}

    @GetMapping("/get/todo")
        public ResponseEntity<?>getTodosByUser(HttpServletRequest request){
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            User currentUser = userRepository.findByUsername(username);
            if (currentUser != null) {
           List<Todo>todos = todoRepository.findAllByUser(currentUser);
                log.info("Retrieved {} todos for user {}", todos.size(), username);
                List<TodoRes> todoResponses = todos.stream().map(todo -> {
                    TodoRes todoRes = new TodoRes();
                    todoRes.setId(todo.getId());
                    todoRes.setTitle(todo.getTitle());
                    todoRes.setTask(todo.getTask());
                    return todoRes;
                        }).collect(Collectors.toList());
                return ResponseEntity.ok().body(todoResponses);

            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }


    @PutMapping("/add/task/{todoid}")
        public ResponseEntity<?> addTask(HttpServletRequest request , @PathVariable Long todoid, @RequestBody List<Task> tasks) {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                log.info("TOKEN AUTHORIZED");
                if (jwtUtil != null && jwtUtil.validateToken(token)) {
                   Todo todo = todoRepository.findById(todoid).orElseThrow(() -> new EntityNotFoundException("Todo not found with id : " + todoid));;
                       todo.setTask(tasks);
                       todoRepository.save(todo);
                       return ResponseEntity.status(HttpStatus.CREATED).body("Task creasted ans assigned to Todo with id : {}" + todoid);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }

    @PutMapping("/add/todo")
        public ResponseEntity<?> addTodo(HttpServletRequest request , @RequestParam List todo){
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            log.info("TOKEN AUTHORIZED");
            if (jwtUtil != null && jwtUtil.validateToken(token)) {
                User currentUser = SecurityUtils.getCurrentUser();
                currentUser.setTodos(todo);
                userRepository.save(currentUser);
                return ResponseEntity.status(HttpStatus.CREATED).body("");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }

    @PutMapping("/update/todo/{todoid}")
    public ResponseEntity<?> updateTodo(HttpServletRequest request , @PathVariable Long todoid , @RequestParam Todo updatedTodo) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            log.info("TOKEN AUTHORIZED");
            if (jwtUtil != null && jwtUtil.validateToken(token)) {
                Todo existingTodo = todoRepository.findById(todoid).orElseThrow(()->new EntityNotFoundException("Todo not found with id : " + todoid));
                if (updatedTodo.getTitle() != null){
                    existingTodo.setTitle(updatedTodo.getTitle());
                }
                todoRepository.save(existingTodo);
                return ResponseEntity.status(HttpStatus.OK).body("Todo updated with id : " + todoid);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }


@PutMapping("/update/task/{taskid}/{todoid}")
public ResponseEntity<?> updateTaskOnTodo(HttpServletRequest request , @PathVariable Long taskid , @PathVariable Long todoid , @RequestBody Task updatedTask){
    String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.substring(7);
        log.info("TOKEN AUTHORIZED");
        if (jwtUtil != null && jwtUtil.validateToken(token)) {
            Task existingTask = taskRepository.findById(taskid).orElseThrow(() -> new EntityNotFoundException("Task not found with id : " + taskid));
            Todo existingTodo = todoRepository.findById(todoid).orElseThrow(() -> new EntityNotFoundException("Todo not found with id : " + todoid));

            if (existingTask.getTodo().getId().equals(existingTodo.getId())) {
                if(updatedTask.getDescription() != null){
                    existingTask.setDescription(updatedTask.getDescription());
                }
                if(updatedTask.getContent() != null){
                    existingTask.setContent(updatedTask.getContent());
                }
                if(updatedTask.isCompleted() != existingTask.isCompleted()){
                    existingTask.setCompleted(updatedTask.isCompleted());
                }
                taskRepository.save(existingTask);
                return ResponseEntity.status(HttpStatus.OK).body("Task updated with id : " + taskid);
            }else {
                throw new RuntimeException("Task and Todo ids do not match");
            }
        }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
}
    @DeleteMapping("/delete/todo/{todoid}")
    public ResponseEntity<?> deleteTodo(HttpServletRequest request , @PathVariable Long todoid) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            log.info("TOKEN AUTHORIZED");
            if (jwtUtil != null && jwtUtil.validateToken(token)) {
                Todo existingTodo = todoRepository.findById(todoid).orElseThrow(()->new EntityNotFoundException("Todo not found with id : " + todoid));
                todoRepository.deleteById(todoid);
                return ResponseEntity.status(HttpStatus.OK).body("Todo deleted with id : " + todoid);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }


    @DeleteMapping("/delete/task/{taskid}/{todoid}")
    public ResponseEntity<?> deleteTask(HttpServletRequest request, @PathVariable Long taskid, @PathVariable Long todoid) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            log.info("TOKEN AUTHORIZED");
            if (jwtUtil != null && jwtUtil.validateToken(token)) {
                Task existingTask = taskRepository.findById(taskid).orElseThrow(() -> new EntityNotFoundException("Task not found with id : " + taskid));
                Todo existingTodo = todoRepository.findById(todoid).orElseThrow(() -> new EntityNotFoundException("Todo not found with id : " + todoid));
                if (existingTask.getTodo().getId().equals(existingTodo.getId())) {
                    taskRepository.deleteById(taskid);
                    return ResponseEntity.status(HttpStatus.OK).body("Task deleted with id : " + taskid);
                } else {
                    throw new RuntimeException("Task and Todo ids do not match");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }

}

