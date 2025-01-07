package org.georges.georges.Todo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Todo.Tasks.Task;
import org.georges.georges.User.User;

import java.util.List;
@Entity
@Getter
@Setter
@Slf4j
public class Todo {
    @Id
    private Long id ;
    private String title;

    @JsonManagedReference
    @OneToMany(mappedBy = "todo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> task;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = true, updatable = true)
    private User user;
}
