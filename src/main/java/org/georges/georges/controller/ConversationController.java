package org.georges.georges.controller;

import org.georges.georges.pojos.CustomUserDetails;
import org.georges.georges.pojos.Message;
import org.georges.georges.pojos.User;
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
    @GetMapping("/message")
    public String chat(Model model){
         List<Message> messages = messageService.getMessagesForCurrentUser();
         model.addAttribute("messages", messages);
        return "chat";
    }
    @PostMapping("/send-message")
    public String sendMessage(@RequestParam("prompt") String prompt, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User currentUser = userDetails.getUser();

            // Logique pour envoyer le message
            messageService.sendMessage(currentUser, currentUser, prompt);

            // Après avoir envoyé le message, récupérez à nouveau la liste des messages et mettez à jour le modèle
            List<Message> messages = messageService.getMessagesForCurrentUser();
            if (messages != null) {
                model.addAttribute("messages", messages);
                logger.info("Le message est {}:" ,messages);
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
