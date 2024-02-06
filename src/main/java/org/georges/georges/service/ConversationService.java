package org.georges.georges.service;

import org.georges.georges.pojos.Conversation;
import org.georges.georges.pojos.User;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class ConversationService {


    public Conversation createConversation(List<User> participants){
        Conversation conversation = new Conversation();
        conversation.setParticipants(new HashSet<>(participants));
        return conversation.save(conversation);
    }
}
