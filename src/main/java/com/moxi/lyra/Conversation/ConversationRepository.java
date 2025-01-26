package com.moxi.lyra.Conversation;

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
    Conversation findByParticipantsContaining(User user);
    @Query("SELECT c FROM Conversation c WHERE  c.senderId IN :participantsIDs AND c.receiverId IN : participantsIDs")
    List<Conversation> findBySenderIdAndReceiverId(@Param("participantsIDs") Set<Long> participantIds);
    @Query("SELECT c FROM Conversation c WHERE c.senderId IN :senderID")
    List<Conversation> findBySenderId(@Param("senderID") Long senderId);
}
