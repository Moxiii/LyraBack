package com.moxi.lyra.Todo;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.moxi.lyra.Todo.Tasks.TaskRepository;
import com.moxi.lyra.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TodoService {
    @Autowired
    private TaskRepository taskRepository;
@Autowired
    private TodoRepository todoRepository;

    @Transactional
    public void deleteTodoWithTasks(Long todoID) {
        try {
            taskRepository.deleteAllByTodoId(todoID);
        } catch (Exception e) {
            log.error("Error deleting tasks for Todo with ID {}: {}", todoID, e.getMessage());
            throw e;
        }
        try {
            todoRepository.manuallyDeleteTodoByID(todoID);
        } catch (Exception e) {
            log.error("Error deleting Todo with ID {}: {}", todoID, e.getMessage());
            throw e;
        }
    }
    public List<Todo> findTodosByUser(User user){
       List<Todo> todos = todoRepository.findAllByUser(user);
       return todos;
    }
    public void deleteAllTodosByUSer(User user){
        List<Todo> todos = findTodosByUser(user);
        for (Todo todo : todos) {
            deleteTodoWithTasks(todo.getId());
        }
    }
}

