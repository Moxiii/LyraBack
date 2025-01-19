package org.georges.georges.DTO;

import lombok.Getter;
import lombok.Setter;
import org.georges.georges.Todo.Tasks.Task;

import java.util.List;

@Getter
@Setter
public class TodoRes {
    private Long id;
    private String title;
    private List<Task> tasks;
}
