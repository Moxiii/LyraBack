package com.moxi.lyra.Todo;

import jakarta.transaction.Transactional;
import com.moxi.lyra.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo , Long> {
List<Todo> findAllByUser(User user);
@Transactional
@Modifying
@Query("DELETE FROM Todo t WHERE t.id = :todoID")
void manuallyDeleteTodoByID(@Param("todoID") Long todoID);
}
