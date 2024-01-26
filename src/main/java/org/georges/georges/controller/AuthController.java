package org.georges.georges.controller;

import org.georges.georges.pojos.User;
import org.georges.georges.repository.UserRepository;
import org.georges.georges.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.logging.Logger;



@Controller
@RequestMapping("/auth")
public class AuthController {
    Logger log = Logger.getLogger(AuthController.class.getName());
    @Autowired
    private UserRepository userRepository;
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
@GetMapping(path = {"/register"})
public String register(Model model){
    model.addAttribute("user", new User());
        return "register";
}
@PostMapping(path= {"/process_register"})
public String processRegister(User user){
    BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
    String psswdEncoded = passwordEncoder.encode(user.getPassword());
    user.setPassword(psswdEncoded);
    if (userRepository.existsByPseudo(user.getPseudo())) {
        return "login";
    }

    userRepository.save(user);
        return "register_success";
}
    @PostMapping(path = {"/login"})
    public String inscriptionSubmit(@ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Vérifier si l'utilisateur existe dans votre système
        User existingUser = userService.findByUsername(user.getPseudo());
        // Définir manuellement la valeur de pseudo si elle est null

        if (existingUser != null) {
            // L'utilisateur existe, vérifier le mot de passe
            if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {

                userConnecte = existingUser;
                model.addAttribute("membreConnecte", userConnecte);
                return "redirect:/";
            } else {
                model.addAttribute("error", true);
                return "login";
            }
        } else {
            return "redirect:/auth/register";
        }
    }

    //gestion du membre connecte:
    @ModelAttribute("membreConnecte")
    public User membreConnecte() {
        return userConnecte;
    }
}
