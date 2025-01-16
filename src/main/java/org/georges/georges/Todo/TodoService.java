package org.georges.georges.Todo;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Todo.Tasks.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            todoRepository.manualyDeleteTodoByID(todoID);
        } catch (Exception e) {
            log.error("Error deleting Todo with ID {}: {}", todoID, e.getMessage());
            throw e;
        }
    }
}

