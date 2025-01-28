package com.moxi.lyra.Conversation.Message;

import com.moxi.lyra.Conversation.Conversation;
import com.moxi.lyra.Conversation.ConversationService;
import com.moxi.lyra.User.User;
import lombok.extern.slf4j.Slf4j;

import com.moxi.lyra.Mongo.Message.MessageMongoRepository;
import com.moxi.lyra.Mongo.Message.MongoMessage;
import com.moxi.lyra.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
public class MessageService {
    @Autowired
    private MessageMongoRepository messageMongoRepository;
    @Autowired
    private  MessageRepository messageRepository;
    @Autowired
    private UserService userService;
@Autowired
private ConversationService conversationService;

public void saveMongoMessage(MongoMessage message){
        messageMongoRepository.save(message);
    }

}
