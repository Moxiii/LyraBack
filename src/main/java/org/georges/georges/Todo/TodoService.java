package org.georges.georges.Todo;

import org.georges.georges.Todo.Tasks.Task;
import org.georges.georges.Todo.Tasks.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Service
@RequestMapping("api/todo")
public class TodoService {
    @Autowired
    private TaskRepository taskRepository;
@Autowired
    private TodoRepository todoRepository;

}
