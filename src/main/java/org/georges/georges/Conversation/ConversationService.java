package org.georges.georges.Conversation;

import org.georges.georges.Conversation.Message.MessageRepository;
import org.georges.georges.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {
@Autowired
private  ConversationRepository conversationRepository;
@Autowired
private  UserRepository userRepository;
@Autowired
private  MessageRepository messageRepository;




    }
