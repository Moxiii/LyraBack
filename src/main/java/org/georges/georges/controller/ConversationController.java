package org.georges.georges.controller;

import org.georges.georges.config.SecurityUtils;
import org.georges.georges.pojos.CustomUserDetails;
import org.georges.georges.pojos.Message;
import org.georges.georges.pojos.User;
import org.georges.georges.repository.UserRepository;
import org.georges.georges.service.MessageService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/chat/")
@Controller
public class ConversationController {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MessageService messageService ;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/message")
    public String chat(Model model){
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser != null){
            Long receiverId = currentUser.getId();
            User receiverUser = userRepository.findById(receiverId).orElse(null);
            if (receiverUser != null){
                List<Message> messages = messageService.getMessagesBetweenUsers(currentUser.getId(), receiverUser.getId());
                model.addAttribute("messages", messages);
                logger.warn("Les messages ont été récupérés avec succès : {}", messages);
            }else{
                logger.warn("L'utilisateur destinataire n'a pas été trouvé.");
            }
            }else{
            logger.warn("L'utilisateur actuel n'est pas authentifié.");
            // Rediriger vers la page de connexion si l'utilisateur n'est pas authentifié
            return "redirect:/login";
        }
        return "chat";
        }

    @PostMapping("/send-message")
    public String sendMessage(@RequestParam("prompt") String prompt, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User currentUser = userDetails.getUser();

            // Logique pour envoyer le message


            // Après avoir envoyé le message, récupérez à nouveau la liste des messages et mettez à jour le modèle
            List<Message> messages = messageService.getMessagesForCurrentUser();
            if (messages != null) {
                // Ajouter le nouveau message à la liste existante
                messageService.sendMessage(currentUser, currentUser, prompt);

                model.addAttribute("messages", messages);

                logger.info("Le message est {}:" ,messages);
                logger.info("Le prompt est {}",prompt);
                logger.info("de {}: " , currentUser.getPseudo());
            } else {
                logger.warn("No message");
            }
            return "redirect:/chat/message";

        } else {
            // Gérez le cas où l'authentification n'est pas correcte
            return "redirect:/login"; // Redirigez vers la page de connexion, par exemple
        }
    }
}
