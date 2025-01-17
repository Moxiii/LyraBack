package org.georges.georges.Conversation.Message;

import lombok.extern.slf4j.Slf4j;
import org.georges.georges.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MessageService {
    @Autowired
    private  MessageRepository messageRepository;
    @Autowired
    private  UserRepository userRepository;


}
