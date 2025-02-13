package com.moxi.lyra.Conversation;

import com.moxi.lyra.DTO.Conversation.ConversationWithCount;
import com.moxi.lyra.User.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation , Long > {
@Query("SELECT c FROM Conversation c LEFT JOIN FETCH c.participants WHERE c IN :conversations")
List<Conversation> findConversationsWithParticipants(@Param("conversations") List<Conversation> conversations);

@Query("SELECT c.id, COUNT(p) FROM Conversation c LEFT JOIN c.participants p WHERE c IN :conversations GROUP BY c.id")
List<Object[]> countParticipantsForConversations(@Param("conversations") List<Conversation> conversations);

@Query("SELECT c FROM Conversation c LEFT JOIN FETCH c.participants LEFT JOIN FETCH c.messages WHERE c.id = :id")
Optional<Conversation> findById(@Param("id") Long id);

List<Conversation> findByParticipantsContaining(User user);
@Query("SELECT new com.moxi.lyra.DTO.Conversation.ConversationWithCount(c, (SELECT COUNT(p2) FROM c.participants p2)) " +
		"FROM Conversation c " +
		"WHERE EXISTS (SELECT 1 FROM c.participants p WHERE p IN :participants)")
List<ConversationWithCount> findByParticipants(@Param("participants") Set<User> participants);

}
