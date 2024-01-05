package org.georges.georges.controller;

import org.georges.georges.pojos.User;
import org.georges.georges.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
@RequestMapping("/")
public class AuthController {
    Logger log = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private UserService userService;
    private User userConnecte;

    @GetMapping(path = {"/", "/index"})
    public String index() {

        return "index";
    }

    @GetMapping(path = {"/login"})
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping(path = {"/login"})
    public String inscriptionSubmit(@ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        // Vérifier si l'utilisateur existe dans votre système
        User existingUser = userService.findByUsername(user.getPseudo());
        // Définir manuellement la valeur de pseudo si elle est null
        if (user.getPseudo() == null && existingUser != null) {
            user.setPseudo(existingUser.getPseudo());
             existingUser = userService.findByUsername(user.getPseudo());
        }
        if (existingUser != null) {
            // L'utilisateur existe, vérifier le mot de passe
            if (existingUser.getPassword().equals(user.getPassword())) {

                userConnecte = existingUser;
                model.addAttribute("membreConnecte", userConnecte);
                return "redirect:/index";
            } else {
                model.addAttribute("error", true);
                return "login";
            }
        } else {
            return "redirect:/register";
        }
    }

    //gestion du membre connecte:
    @ModelAttribute("membreConnecte")
    public User membreConnecte() {
        return userConnecte;
    }
}
