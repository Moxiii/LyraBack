package com.moxi.lyra.Conversation.Message;

import com.moxi.lyra.Conversation.Conversation;
import com.moxi.lyra.Conversation.ConversationService;
import com.moxi.lyra.DTO.Conversation.ConversationWithCount;
import com.moxi.lyra.User.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.moxi.lyra.Mongo.Message.MessageMongoRepository;
import com.moxi.lyra.Mongo.Message.MongoMessage;
import com.moxi.lyra.User.UserService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@EnableScheduling
public class MessageService {
    @Autowired
    private MessageMongoRepository messageMongoRepository;
    @Autowired
    private  MessageRepository messageRepository;
    @Autowired
    private UserService userService;
@Autowired
private ConversationService conversationService;
private final int MONGO_MESSAGE_LIMIT = 20;
private final int MESSAGE_RETENTION_TIME_LIMIT = 5;
public void saveMongoMessage(MongoMessage message){
    long messageCount = messageMongoRepository.count();
    messageMongoRepository.save(message);
    if(messageCount >= MONGO_MESSAGE_LIMIT){
        transfertOldMessageToSql();
    }
    }
@Transactional
public Message convertToMysqlMessage(MongoMessage mongoMessage) {
    Message message = new Message();
    message.setContent(mongoMessage.getContent());
    message.setTimestamp(mongoMessage.getTimestamp());

    Set<User> users = new HashSet<>();

    User sender = userService.findByUsername(mongoMessage.getSender());
    log.warn("SENDER MONGO: " + sender.getUsername());
    message.setSender(sender);

    users.add(sender);

    List<ConversationWithCount> existingConversations = conversationService.findByParticipants(users);

    Conversation conversation = existingConversations.stream()
            .filter(convWithCount -> {
                Conversation conv = convWithCount.getConversation();
                Hibernate.initialize(conv.getParticipants());
                Long participantsCount = convWithCount.getCount();
                if (participantsCount >= 3) {
                    users.addAll(conv.getParticipants());
                } else {
                    User receiver = userService.findByUsername(mongoMessage.getReceiver());
                    if (receiver != null) {
                        users.add(receiver);
                    } else {
                        log.error("Receiver user not found for username: {}", mongoMessage.getReceiver());
                    }
                }
                Set<Long> participantsIds = conv.getParticipants().stream()
                        .map(User::getId)
                        .collect(Collectors.toSet());
                Set<Long> usersIds = users.stream()
                        .map(User::getId)
                        .collect(Collectors.toSet());

                return participantsCount.equals((long) users.size()) && participantsIds.equals(usersIds);
            })
            .map(ConversationWithCount::getConversation)
            .findFirst()
            .orElseGet(() -> {
                Conversation newConv = new Conversation();
                newConv.setParticipants(users);
                return newConv;
            });

    if (!conversation.getMessages().contains(message)) {
        conversation.getMessages().add(message);
    }

    conversationService.save(conversation);
    message.setConversation(conversation);
    messageRepository.save(message);

    return message;
}



    @Scheduled(fixedRate = 300000)
    public void scheduledMongoToMysqlTransfert(){
    transfertOldMessageToSql();
    }
public void transfertOldMessageToSql(){
    List<MongoMessage> oldMessages = messageMongoRepository.findAll();
    if(!oldMessages.isEmpty()){
        List<Message> messagesToSave = oldMessages.stream()
                .map(this::convertToMysqlMessage).toList();
        messageRepository.saveAll(messagesToSave);
        messageMongoRepository.deleteAll(oldMessages);
    }

}
public List<MongoMessage> getRecentMessages() {
    LocalDateTime threshold = LocalDateTime.now().minusMinutes(MESSAGE_RETENTION_TIME_LIMIT);
    return messageMongoRepository.findByTimestampAfter(threshold);
}
public List<Message> getOldMessages() {
    return messageRepository.findAllByOrderByTimestampAsc();
}
}
