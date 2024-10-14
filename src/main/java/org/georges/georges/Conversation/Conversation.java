package org.georges.georges.Conversation;

import jakarta.persistence.*;
import lombok.*;
import org.georges.georges.Conversation.Message.Message;
import org.georges.georges.User.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

@Getter
@Setter
@Entity
public class Conversation {
    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "user_conversation",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "conversation_id")
    private List<Message> messages = new ArrayList<>();

}
