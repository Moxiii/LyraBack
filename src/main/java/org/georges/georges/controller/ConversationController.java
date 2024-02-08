package org.georges.georges.controller;

import lombok.extern.slf4j.Slf4j;
import org.georges.georges.config.SecurityUtils;
import org.georges.georges.pojos.Conversation;
import org.georges.georges.pojos.Message;
import org.georges.georges.pojos.User;
import org.georges.georges.repository.MessageRepository;
import org.georges.georges.repository.UserRepository;
import org.georges.georges.service.ConversationService;
import org.georges.georges.service.MessageService;
import org.georges.georges.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
public class ConversationController {

    @Autowired
    private MessageService messageService ;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private MessageRepository messageRepository;


    @GetMapping("/message")
    public String chat(Model model) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null) {
            // Récupérer les conversations de l'utilisateur actuel
            List<Conversation> conversations = conversationService.findConversationsByParticipantId(currentUser.getId());

            // Créer un ensemble pour stocker tous les identifiants des participants
            Set<Long> participantIds = new HashSet<>();

            // Parcourir les conversations pour obtenir les identifiants des participants
            for (Conversation conversation : conversations) {
                for (User participant : conversation.getParticipants()) {
                    // Ajouter l'identifiant du participant à l'ensemble
                    participantIds.add(participant.getId());
                }
            }

            // Utiliser l'ensemble des identifiants des participants pour récupérer toutes les conversations
            Conversation conversation = conversationService.createOrGetConversation(participantIds);
            List<Message> messages = conversation.getMessages();
            model.addAttribute("messages", messages);
        } else {
            log.warn("L'utilisateur actuel n'est pas authentifié.");
            // Rediriger vers la page de connexion si l'utilisateur n'est pas authentifié
            return "redirect:/login";
        }
        return "chat";
    }


    @PostMapping("/chat/search-by-email" )
    public String searchAndSendMessage(@RequestParam("email") String email, @RequestParam("message") String messageContent) {
        User currentUser = SecurityUtils.getCurrentUser();
        // Recherchez l'utilisateur par email
        User receiver = userRepository.findByEmail(email);
        if (receiver != null) {
            // Créez une conversation ou récupérez-en une existante
            Conversation conversation = conversationService.createOrGetConversation(Set.of(currentUser.getId(), receiver.getId()));

            // Créez et enregistrez le message
            Message newMessage = new Message(currentUser, receiver, messageContent);
            newMessage.setTimestamp(new Date()); // Définir le timestamp actuel
            messageRepository.save(newMessage); // Enregistrer le message dans la base de données

            return "redirect:/chat"; // Redirigez vers la page de chat après l'envoi du message
        } else {
            log.info("L'utilisateur n'existe pas.");
            // L'utilisateur avec l'email spécifié n'existe pas, gérer l'erreur ou afficher un message à l'utilisateur
            return "redirect:/chat"; // ou redirigez vers une page d'erreur
        }
    }




    @PostMapping("/send-message")
    public String sendMessage(
            @RequestParam("participantId") Long participantId,
            @RequestParam("content") String content,
            Model model) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null ) {
            // Après avoir envoyé le message, récupérez à nouveau la liste des messages et mettez à jour le modèle
            List<Message> messages = messageService.getMessagesForCurrentUser();

            if (messages != null) {
                User receiver  = userService.getUserById(participantId);
                if (receiver == null){
                    return "redirect:/chat/message";
                }
                // Ajouter le nouveau message à la liste existante
                messageService.sendMessage(currentUser, receiver, content);

                model.addAttribute("messages", messages);

                log.info("Le message est {}:" ,messages);
                log.info("Le prompt est {}",content);
                log.info("de {}: " , currentUser.getUsername());
            } else {
                log.warn("No message");
            }
            return "redirect:/chat/message";

        } else {
            // Gérez le cas où l'authentification n'est pas correcte
            return "redirect:/login"; // Redirigez vers la page de connexion, par exemple
        }
    }

    @PostMapping("/search-participants")
    public String searchParticipant(@RequestParam("query")  String query ,Model model ){
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null ) {
            List<User> participants = userService.searchParticipants(query);
            model.addAttribute("participants", participants);
            return "participants";
        }else {
            return "redirect:/";
        }
    }

    @MessageMapping("/private-message")
    public Message receivePrivateMessage(@Payload Message message){
        return message;
    }
}

