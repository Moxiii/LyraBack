package com.moxi.lyra.Conversation.Message;

import lombok.extern.slf4j.Slf4j;

import com.moxi.lyra.Mongo.Message.MessageMongoRepository;
import com.moxi.lyra.Mongo.Message.MongoMessage;
import com.moxi.lyra.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MessageService {
    @Autowired
    private MessageMongoRepository messageMongoRepository;
    @Autowired
    private  MessageRepository messageRepository;
    @Autowired
    private UserService userService;

    public void saveMongoMessage(MongoMessage message){
        messageMongoRepository.save(message);
    }

}
