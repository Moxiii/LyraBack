package org.georges.georges.Todo.Tasks;

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
    private boolean completed;
    @ManyToOne
    @JoinColumn(name = "todo_id")
    private Todo todo;


    public Task(String description , boolean completed){
        this.description = description;
        this.completed = completed;
    }
    public Task(){}
}
