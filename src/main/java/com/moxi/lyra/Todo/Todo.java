package com.moxi.lyra.Todo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import com.moxi.lyra.Todo.Tasks.Task;
import com.moxi.lyra.User.User;

import java.util.List;
@Entity
@Getter
@Setter
public class Todo {
    @Id
    private Long id ;
    private String title;

    @JsonManagedReference
    @OneToMany(mappedBy = "todo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = true, updatable = true)
    private User user;
}
