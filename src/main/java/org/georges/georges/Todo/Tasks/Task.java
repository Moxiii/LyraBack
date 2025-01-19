package org.georges.georges.Todo.Tasks;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.georges.georges.Todo.Todo;

@Entity
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String content;
    private boolean completed;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "todo_id")
    private Todo todo;

    public Task(){}
}
