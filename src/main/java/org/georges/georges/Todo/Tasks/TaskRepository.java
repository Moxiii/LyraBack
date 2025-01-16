package org.georges.georges.Todo.Tasks;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
     List<Task> findByCompletedTrue();
     List<Task> findByCompletedFalse();
     List<Task> findAll();

     Task getById(Long id);

    List<Task> findByTodoId(Long todoId);
    @Transactional
    @Modifying
    @Query("DELETE FROM Task t WHERE t.todo.id = :todoID")
    void deleteAllByTodoId(@Param("todoID") Long todoID);
}