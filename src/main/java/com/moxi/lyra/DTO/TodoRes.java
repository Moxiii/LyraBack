package com.moxi.lyra.DTO;

import lombok.Getter;
import lombok.Setter;
import com.moxi.lyra.Todo.Tasks.Task;

import java.util.List;

@Getter
@Setter
public class TodoRes {
    private Long id;
    private String title;
    private List<Task> tasks;
}
