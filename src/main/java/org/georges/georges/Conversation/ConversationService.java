package org.georges.georges.Conversation;

import jakarta.transaction.Transactional;
import org.georges.georges.Conversation.Message.Message;
import org.georges.georges.User.User;
import org.georges.georges.Conversation.Message.MessageRepository;
import org.georges.georges.User.UserRepository;
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
        Optional<Conversation> conversationOptional = conversationRepository.findBySenderIdAndReceiverId(senderId, recipientId);

        if (conversationOptional.isPresent()) {
            Conversation conversation = conversationOptional.get();
            return conversation.getMessages();
        } else {
            // Gérer le cas où aucune conversation n'est trouvée
            return Collections.emptyList();
        }
    }

    public Optional<String> getConversationId(Long senderId, Long receiverId, boolean createNewConvIfNotExists) {
        Optional<Conversation> conversationOptional = conversationRepository.findBySenderIdAndReceiverId(senderId, receiverId);

        if (conversationOptional.isPresent()) {
            // Renvoyer l'identifiant de la conversation s'il existe
            return conversationOptional.map(conversation -> conversation.getId().toString());
        } else {
            if (createNewConvIfNotExists) {
                // Créer une nouvelle conversation et renvoyer son identifiant
                Conversation newConversation= createConversation(senderId, receiverId);
                return Optional.of(newConversation.getId().toString());
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
    public List<Message> getAllMessagesByReceiverId(Long receiverId) {
        return messageRepository.findByReceiverIdOrderByTimestampAsc(receiverId);
    }

    public Conversation createOrGetConversation(Set<Long> participantIds) {
        // Rechercher toutes les conversations impliquant ces participants
        List<Conversation> existingConversations = findConversationsByParticipantIds(participantIds);

        if (!existingConversations.isEmpty()) {
            // Si des conversations existent déjà, renvoyer la première conversation trouvée
            return existingConversations.get(0);
        } else {
            // Si aucune conversation n'existe, créer une nouvelle conversation
            Conversation newConversation = new Conversation();
            newConversation.setParticipants(new HashSet<>());

            // Ajouter les participants à la conversation
            for (Long participantId : participantIds) {
                Optional<User> participant = userRepository.findById(participantId);
                participant.ifPresent(user -> newConversation.getParticipants().add(user));
            }

            // Enregistrer la conversation dans la base de données
            return conversationRepository.save(newConversation);
        }
    }


    public List<Conversation> findConversationsByParticipantId(Long userId) {
        // Recherchez toutes les conversations où l'utilisateur est l'expéditeur ou le destinataire
        return conversationRepository.findBySenderIdOrReceiverId(Collections.singleton(userId));
    }


    @Transactional
    public List<Conversation> findConversationsByParticipantIds(Set<Long> participantIds) {
        // Recherchez les utilisateurs en fonction de leurs identifiants
        List<User> participants = userRepository.findAllById(participantIds);

        // Recherchez toutes les conversations impliquant ces participants
        return conversationRepository.findBySenderIdOrReceiverId(participantIds);
    }

    private List<Message> findMessagesByParticipants(Set<Long> participantIds) {
        // Recherchez tous les messages entre les participants
        return messageRepository.findBySenderIdInAndReceiverIdIn(participantIds, participantIds);

    }}
