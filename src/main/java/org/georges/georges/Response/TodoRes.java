package org.georges.georges.Response;

import lombok.Getter;
import lombok.Setter;
import org.georges.georges.Todo.Tasks.Task;

import java.util.List;

@Getter
@Setter
public class TodoRes {
    private Long id;
    private String title;
    private List<Task> task;
    private byte[] projectPicture;
}
