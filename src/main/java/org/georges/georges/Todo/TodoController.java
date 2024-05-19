package org.georges.georges.Todo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Todo.Tasks.Task;
import org.georges.georges.Todo.Tasks.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private JwtUtil jwtUtil;
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

}
