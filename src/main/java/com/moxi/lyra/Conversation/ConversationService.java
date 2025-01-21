package com.moxi.lyra.Conversation;

import com.moxi.lyra.Conversation.Message.MessageService;
import com.moxi.lyra.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {
@Autowired
private  ConversationRepository conversationRepository;
@Autowired
private UserService userService;
@Autowired
private MessageService messageService;




    }
