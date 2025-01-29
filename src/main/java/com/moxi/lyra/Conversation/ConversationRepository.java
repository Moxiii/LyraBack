package com.moxi.lyra.Conversation;

import com.moxi.lyra.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation , Long > {
@Query("SELECT c , COUNT(p) FROM Conversation c JOIN FETCH c.participants p WHERE p IN :participants GROUP BY c")
List<Object[]> findByParticipants(@Param("participants") Set<User> participants);

List<Conversation> findByParticipantsContaining(User user);
}
