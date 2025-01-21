package com.moxi.lyra.Conversation.Message.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    @Autowired
    private SimpMessagingTemplate template;

}
