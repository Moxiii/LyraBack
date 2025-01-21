package org.georges.georges.Conversation;

import org.georges.georges.Conversation.Message.MessageService;
import org.georges.georges.User.UserService;
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
