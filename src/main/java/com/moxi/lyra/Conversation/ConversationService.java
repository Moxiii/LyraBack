package com.moxi.lyra.Conversation;

import com.moxi.lyra.Conversation.Message.MessageService;
import com.moxi.lyra.DTO.Conversation.ConversationWithCount;
import com.moxi.lyra.User.User;
import com.moxi.lyra.User.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
@Slf4j
@Service
public class ConversationService {
@Autowired
private ConversationRepository conversationRepository;
@Autowired
private UserService userService;
@Autowired
private MessageService messageService;


public List<Conversation> findByUser(User user) {
    return conversationRepository.findByParticipantsContaining(user);
}

public List<ConversationWithCount> findByParticipants(Set<User> users) {
    List<ConversationWithCount> conversations = conversationRepository.findByParticipants(users);
    conversations.forEach(conv ->
            log.warn("Conv : {} - Participants: {}", conv.getConversation().getName(), conv.getCount()));
    return conversations;
}
public void save(Conversation conversation) {
    conversationRepository.save(conversation);
}
public Conversation findById(Long id) {
    return conversationRepository.findById(id)
    .orElseThrow(() -> new RuntimeException("Conversation non trouvée avec ID : " + id));

    }

}
