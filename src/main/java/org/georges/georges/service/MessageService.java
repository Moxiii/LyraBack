package org.georges.georges.service;

import lombok.extern.slf4j.Slf4j;
import org.georges.georges.controller.AuthController;
import org.georges.georges.pojos.Conversation;
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
@Slf4j
@Service
public class MessageService {

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
    public Message saveMessage(Message message) {
        // Assurez-vous que les utilisateurs existent dans la base de données
        if (message.getSender() != null && message.getReceiver() != null) {
            Optional<User> optionalSender = userRepository.findById(message.getSender().getId());
            Optional<User> optionalReceiver = userRepository.findById(message.getReceiver().getId());

            if (optionalSender.isPresent() && optionalReceiver.isPresent()) {
                message.setSender(optionalSender.get());
                message.setReceiver(optionalReceiver.get());
                message.setTimestamp(new Date());

                // Sauvegarder le message dans la base de données
                return messageRepository.save(message);
            } else {
                log.warn("Le sender est ou le receiver nest pas present ");
                return null;
            }
        } else {
            // Gérer le cas où l'un des expéditeurs ou des destinataires est null
            // ou renvoyer null ou jeter une exception, selon votre logique métier
            log.warn("Le sender est ou le receiver est vide ");
            return null;
        }
    }

    public List<Message> getMessagesForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        // Récupérez tous les messages associés à l'utilisateur actuel (en tant qu'expéditeur ou destinataire)
        List<Message> sentMessages = messageRepository.findBySender(currentUser);
        List<Message> receivedMessages = messageRepository.findByReceiver(currentUser);

        // Fusionnez les deux listes de messages
        List<Message> allMessages = new ArrayList<>(sentMessages);
        allMessages.addAll(receivedMessages);

        return allMessages;
    }
    public void sendMessage(User sender, User receiver, String content) {
        // Créez une instance de Message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(new Date());

        // Enregistrez le message dans la base de données
        messageRepository.save(message);
    }
    public void addMessagetoConversation(Message message , Conversation conversation){
        message.setConversation(conversation);
    }
}
