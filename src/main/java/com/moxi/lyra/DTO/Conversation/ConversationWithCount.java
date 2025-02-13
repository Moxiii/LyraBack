package com.moxi.lyra.DTO.Conversation;

import com.moxi.lyra.Conversation.Conversation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConversationWithCount {
private Conversation conversation;
private Long count;
}
