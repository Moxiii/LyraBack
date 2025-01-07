package org.georges.georges.Todo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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

    @GetMapping("/{todoID}")
    public ResponseEntity<?> getTodoByID(HttpServletRequest request , @PathVariable Long todoID){
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
                Todo todo = todoRepository.findById(todoID).orElseThrow(() -> new EntityNotFoundException("Todo not found with id : " + todoID));
                return ResponseEntity.ok().body(todo);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }

    @GetMapping("/get")
        public ResponseEntity<?>getTodosByUser(HttpServletRequest request){
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser != null) {
           List<Todo>todos = todoRepository.findAllByUser(currentUser);
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


    @PostMapping("/add/task/{todoID}")
        public ResponseEntity<?> addTask(HttpServletRequest request , @PathVariable Long todoID, @RequestBody List<Task> tasks) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
                   Todo todo = todoRepository.findById(todoID).orElseThrow(() -> new EntityNotFoundException("Todo not found with id : " + todoID));
                       for(Task task : tasks){
                           task.setTodo(todo);
                       }
                       todo.getTask().addAll(tasks);
                       todoRepository.save(todo);
                       return ResponseEntity.status(HttpStatus.CREATED).body("Task creasted ans assigned to Todo with id : {}" + todoID);
                }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
    @Transactional
    @PostMapping("/add")
        public ResponseEntity<?> addTodo(HttpServletRequest request , @RequestBody Todo todo){
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
            currentUser.getTodos().size();
                todo.setUser(currentUser);
                long todoId = currentUser.getTodos().size() + System.currentTimeMillis();
                todo.setId(Long.parseLong(currentUser.getId()+""+todoId));
                currentUser.getTodos().add(todo);
                userRepository.save(currentUser);
                return ResponseEntity.status(HttpStatus.CREATED).body("Todo created successfully");
            }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }

    @PutMapping("/update/{todoID}")
    public ResponseEntity<?> updateTodo(HttpServletRequest request , @PathVariable Long todoID , @RequestBody Todo updatedTodo) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
                Todo existingTodo = todoRepository.findById(todoID).orElseThrow(()->new EntityNotFoundException("Todo not found with id : " + todoID));
                if (updatedTodo.getTitle() != null){
                    existingTodo.setTitle(updatedTodo.getTitle());
                }
                todoRepository.save(existingTodo);
                return ResponseEntity.status(HttpStatus.OK).body("Todo updated with id : " + todoID);
            }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }


@PutMapping("/update/task/{todoID}/{taskID}")
public ResponseEntity<?> updateTaskOnTodo(HttpServletRequest request , @PathVariable Long taskID , @PathVariable Long todoID , @RequestBody Task updatedTask){
    if(SecurityUtils.isAuthorized(request, jwtUtil)){
            Task existingTask = taskRepository.findById(taskID).orElseThrow(() -> new EntityNotFoundException("Task not found with id : " + taskID));
            Todo existingTodo = todoRepository.findById(todoID).orElseThrow(() -> new EntityNotFoundException("Todo not found with id : " + todoID));

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
                return ResponseEntity.status(HttpStatus.OK).body("Task updated with id : " + taskID);
            }else {
                throw new RuntimeException("Task and Todo ids do not match");
            }
        }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
}
    @DeleteMapping("/delete/{todoID}")
    public ResponseEntity<?> deleteTodo(HttpServletRequest request , @PathVariable Long todoID) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
                Todo existingTodo = todoRepository.findById(todoID).orElseThrow(()->new EntityNotFoundException("Todo not found with id : " + todoID));
                todoRepository.deleteById(todoID);
                return ResponseEntity.status(HttpStatus.OK).body("Todo deleted with id : " + todoID);
            }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }


    @DeleteMapping("/delete/task/{todoID}/{taskID}")
    public ResponseEntity<?> deleteTask(HttpServletRequest request, @PathVariable Long taskID, @PathVariable Long todoID) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
                Task existingTask = taskRepository.findById(taskID).orElseThrow(() -> new EntityNotFoundException("Task not found with id : " + taskID));
                Todo existingTodo = todoRepository.findById(todoID).orElseThrow(() -> new EntityNotFoundException("Todo not found with id : " + todoID));
                if (existingTask.getTodo().getId().equals(existingTodo.getId())) {
                    taskRepository.deleteById(taskID);
                    return ResponseEntity.status(HttpStatus.OK).body("Task deleted with id : " + taskID);
                } else {
                    throw new RuntimeException("Task and Todo ids do not match");
                }
            }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }

}

