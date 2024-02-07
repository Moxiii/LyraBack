package org.georges.georges.service;

import org.georges.georges.pojos.Conversation;
import org.georges.georges.pojos.Message;
import org.georges.georges.pojos.User;
import org.georges.georges.repository.ConversationRepository;
import org.georges.georges.repository.MessageRepository;
import org.georges.georges.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConversationService {

private final ConversationRepository conversationRepository;
private final UserRepository userRepository;
private final MessageRepository messageRepository;

    public ConversationService(ConversationRepository conversationRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public List<Message> findChatMessages(Long senderId, Long recipientId) {
        Optional<Conversation> conversationOptional = conversationRepository.findBySenderIdAndRecipientId(senderId, recipientId);

        if (conversationOptional.isPresent()) {
            Conversation conversation = conversationOptional.get();
            return conversation.getMessages();
        } else {
            // Gérer le cas où aucune conversation n'est trouvée
            return Collections.emptyList();
        }
    }

    public Optional<String> getConversationId(Long senderId, Long receiverId, boolean createNewConvIfNotExists) {
        Optional<Conversation> conversationOptional = conversationRepository.findBySenderIdAndreceiverId(senderId, receiverId);

        if (conversationOptional.isPresent()) {
            // Renvoyer l'identifiant de la conversation s'il existe
            return conversationOptional.map(conversation -> conversation.getConversationId());
        } else {
            if (createNewConvIfNotExists) {
                // Créer une nouvelle conversation et renvoyer son identifiant
                Conversation newConversation= createConversation(senderId, receiverId);
                return Optional.of(newConversation.getConversationId().toString());
            } else {
                // Aucune conversation trouvée et ne pas créer de nouvelle conversation
                return Optional.empty();
            }
        }
    }

    public Conversation createConversation(Long senderId, Long receiverId) {
        // Récupérer les utilisateurs de la base de données
        Optional<User> senderOptional = userRepository.findById(senderId);
        Optional<User> receiverOptional = userRepository.findById(receiverId);

        if (senderOptional.isPresent() && receiverOptional.isPresent()) {
            User sender = senderOptional.get();
            User receiver = receiverOptional.get();
            // Créer un ensemble pour les participants
            Set<User> participants = new HashSet<>();
            participants.add(sender);
            participants.add(receiver);

            // Créer la conversation
            Conversation conversation = new Conversation();
            conversation.setParticipants(participants);

            // Enregistrer la conversation dans la base de données
            return conversationRepository.save(conversation);
        } else {
            // Gérer le cas où l'un des utilisateurs n'existe pas
            throw new IllegalArgumentException("Sender or receiver not found");
        }
    }
}
