package com.moxi.lyra.Conversation;

import com.moxi.lyra.User.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation , Long > {
@Transactional(readOnly = true)
@Query("SELECT c , COUNT(p) FROM Conversation c LEFT JOIN FETCH  c.participants p WHERE p IN :participants GROUP BY c")
List<Object[]> findByParticipants(@Param("participants") Set<User> participants);

@Query("SELECT c FROM Conversation c LEFT JOIN FETCH c.participants LEFT JOIN FETCH c.messages WHERE c.id = :id")
Optional<Conversation> findById(@Param("id") Long id);

List<Conversation> findByParticipantsContaining(User user);
}
