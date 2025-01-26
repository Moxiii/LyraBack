package com.moxi.lyra.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {
    private UserDTO sender;
    private UserDTO receiver;
    private String content;
}
