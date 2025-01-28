package com.moxi.lyra.Conversation.Message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import com.moxi.lyra.Conversation.Conversation;
import com.moxi.lyra.User.User;
import java.util.Date;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "dd-MM-YYYY HH:mm:ss", timezone = "UTC")
    private Date timestamp;

    public Message(User sender, String content , Conversation conversation) {
        this.sender = sender;
        this.content = content;
        this.conversation = conversation;
        this.timestamp = new Date();
    }

}
