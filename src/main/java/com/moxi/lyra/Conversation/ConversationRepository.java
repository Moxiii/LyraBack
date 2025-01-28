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
    List<Conversation> findByUser(User user);
    List<Conversation> findByParticipants(Set<User> participants);


@Query("SELECT c FROM Conversation c WHERE c.participants IN :senderID")
    List<Conversation> findBySenderId(@Param("senderID") Long senderId);

List<Conversation> findByParticipantsContaining(User user);
}
