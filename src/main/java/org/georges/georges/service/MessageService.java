package org.georges.georges.service;

import org.georges.georges.controller.AuthController;
import org.georges.georges.pojos.Message;
import org.georges.georges.pojos.User;
import org.georges.georges.repository.MessageRepository;
import org.georges.georges.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class MessageService {
    Logger log = Logger.getLogger(AuthController.class.getName());
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageService(MessageRepository  messageRepository , UserRepository userRepository){
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }
    public List<Message> getMessagesBetweenUsers(Long senderId, Long receiverId) {
        return messageRepository.findBySenderIdAndReceiverIdOrderByTimestampAsc(senderId, receiverId);
    }
    public void saveMessage(Message message) {
        // Assurez-vous que les utilisateurs existent dans la base de données
        Optional<User> optionalSender = userRepository.findById(message.getSender().getId());
        Optional<User> optionalReceiver = userRepository.findById(message.getReceiver().getId());

        optionalSender.ifPresent(sender -> {
            optionalReceiver.ifPresent(receiver -> {
                message.setSender(sender);
                message.setReceiver(receiver);
                message.setTimestamp(new Date());

                messageRepository.save(message);
            });
        });
    }

    public List<Message> getMessagesForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByPseudo(currentUsername);

        // Récupérez tous les messages associés à l'utilisateur actuel (en tant qu'expéditeur ou destinataire)
        List<Message> sentMessages = messageRepository.findBySender(currentUser);
        List<Message> receivedMessages = messageRepository.findByReceiver(currentUser);

        // Fusionnez les deux listes de messages
        List<Message> allMessages = new ArrayList<>(sentMessages);
        allMessages.addAll(receivedMessages);

        return allMessages;
    }
    public void sendMessage(User sender, User receiver, String prompt) {
        // Créez une instance de Message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setPrompt(prompt);
        message.setTimestamp(new Date());

        // Enregistrez le message dans la base de données
        messageRepository.save(message);
    }
}
