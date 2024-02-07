package org.georges.georges.repository;

import org.georges.georges.pojos.Message;
import org.georges.georges.pojos.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrderByTimestampAsc(Long senderId, Long receiverId);
    List<Message> findByReceiverIdOrderByTimestampAsc(Long receiverId);

    List<Message> findBySender(User currentUser);

    List<Message> findByReceiver(User currentUser);
}
