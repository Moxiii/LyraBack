package com.moxi.lyra.DTO;

import com.moxi.lyra.Conversation.Message.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;

@NoArgsConstructor
@Getter
@Setter
public class MessageDTO {
private Long id;
    private UserDTO sender;
    private UserDTO receiver;
    private String content;
    private String timestamp;

public MessageDTO(Message message) {
    this.id = message.getId();
    this.sender = UserDTO.convertUserToDTO(message.getSender());
    this.content = message.getContent();
    this.timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(message.getTimestamp());
}
}
