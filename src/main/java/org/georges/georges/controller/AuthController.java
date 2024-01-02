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


@Controller
@RequestMapping("/")
public class AuthController {
    @Autowired
    private UserService userService;
    private User userConnecte;

    @GetMapping(path = {"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping(path = {"/login "})
    public String login(Model model) {
        return "login";
    }

    @PostMapping(path = {"/login"})
    public String inscriptionSubmit(@ModelAttribute User user, BindingResult bindingResult) {
        User nouveauUser = userService.ajoutMembre(user);
        userConnecte = nouveauUser;
        if (bindingResult.hasErrors()) {
            return "inscription";
        }
        return "redirect:/index";
    }

    //gestion du membre connecte:
    @ModelAttribute("membreConnecte")
    public User membreConnecte() {
        return userConnecte;
    }
}
