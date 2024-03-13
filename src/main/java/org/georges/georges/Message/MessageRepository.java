package org.georges.georges.Message;

import org.georges.georges.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrderByTimestampAsc(Long senderId, Long receiverId);
    List<Message> findByReceiverIdOrderByTimestampAsc(Long receiverId);

    List<Message> findBySender(User currentUser);

    List<Message> findByReceiver(User currentUser);
    List<Message> findBySenderIdInAndReceiverIdIn(Set<Long> participantIds, Set<Long> participantIds1);

    List<Message> findBySenderOrReceiver(User currentUser, User currentuser);
}
