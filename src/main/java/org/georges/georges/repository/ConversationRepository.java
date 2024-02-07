package org.georges.georges.repository;

import org.georges.georges.pojos.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation , Long > {


    Optional<Conversation> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
