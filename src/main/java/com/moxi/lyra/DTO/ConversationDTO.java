package com.moxi.lyra.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConversationDTO {
private Long id;
private String name ="";
private List<String> participants;
private String lastMessage = "";
}
