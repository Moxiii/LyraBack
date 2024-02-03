package org.georges.georges.controller;

import org.georges.georges.pojos.CustomUserDetails;
import org.georges.georges.pojos.User;
import org.georges.georges.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/")
public class MainController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
@Autowired
    private UserService userService;

    @GetMapping(path = {"/", "/index"})
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication object: {}", authentication);
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated() ) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                String username = userDetails.getUser().getPseudo();
                model.addAttribute("pseudo", username);
                logger.warn("User '{}' is on index controller", username);
            }
        }else {
            logger.warn("Anonymous user !");
        }
        return "index";
    }
    }


