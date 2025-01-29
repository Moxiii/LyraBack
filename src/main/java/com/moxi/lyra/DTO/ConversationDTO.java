package com.moxi.lyra.DTO;

import com.moxi.lyra.Conversation.Conversation;
import com.moxi.lyra.User.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class ConversationDTO {
private Long id;
private String name ="";
private List<String> participants;
private String lastMessage = "";
private List<MessageDTO> messages = new ArrayList<>();

public ConversationDTO(Conversation conversation) {
	this.id = conversation.getId();
	this.name = conversation.getName();
	this.participants = conversation.getParticipants().stream().map(User::getUsername).collect(Collectors.toList());
	this.messages = conversation.getMessages().stream().map(MessageDTO::new).collect(Collectors.toList());
}
}
