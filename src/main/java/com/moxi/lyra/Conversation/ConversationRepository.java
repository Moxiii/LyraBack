package com.moxi.lyra.Conversation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation , Long > {


    Optional<Conversation> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    @Query("SELECT c FROM Conversation c WHERE c.senderId IN :participantIds OR c.receiverId IN :participantIds")
    List<Conversation> findBySenderIdOrReceiverId(@Param("participantIds") Set<Long> participantIds);
}
