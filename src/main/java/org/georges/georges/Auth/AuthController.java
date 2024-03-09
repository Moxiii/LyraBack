package org.georges.georges.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRole.UserRole;
import org.georges.georges.User.UserRole.UserRepository;
import org.georges.georges.User.UserRole.UserRoleRepository;
import org.georges.georges.User.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.authentication.AuthenticationManager;


import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.Optional;
import java.util.logging.Logger;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Slf4j
@RequestMapping("private/auth")
@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    private User userConnecte;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;



    @GetMapping(path = {"/login"})
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/private/auth/login";
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
    if (userRepository.existsByUsername(user.getUsername()) | userRepository.existsByEmail(user.getEmail())) {
        return "login";
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    String formattedDate = dateFormat.format(new Date());
    user.setDateInscription(formattedDate);
    Optional<UserRole> defaultRoleOptional = userRoleRepository.findById(1L);

    if (defaultRoleOptional.isPresent()) {
        UserRole defaultRole = defaultRoleOptional.get();
        user.setUserRole(defaultRole);
    }

    userRepository.save(user);
        return "register_success";
}
    @PostMapping(path = {"/login"})
    public String inscriptionSubmit(@ModelAttribute("user") User user, BindingResult bindingResult, Model model , HttpServletRequest req) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Vérifier si l'utilisateur existe dans votre système
        log.info("Attempting to authenticate user: {}", user.getUsername());
        User existingUser = null;
        if (user.getUsername().contains("@")) {
            existingUser = userService.findByEmail(user.getUsername());
        } else {
            existingUser = userService.findByUsername(user.getUsername());
        }
        // Définir manuellement la valeur de pseudo si elle est null

        if (existingUser != null) {
            // L'utilisateur existe, vérifiez le mot de passe
            if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {

                // Créez un token d'authentification
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(existingUser.getUsername(), user.getPassword());

                try {
                    // Utilisez l'AuthenticationManager pour authentifier l'utilisateur
                    Authentication authentication = authenticationManager.authenticate(token);
                    if (authentication != null && authentication.isAuthenticated()) {
                        // L'utilisateur est authentifié, vous pouvez accéder aux détails d'authentification
                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                        log.info("User is authenticated: {}", userDetails.getUsername());
                        SecurityContext sc = SecurityContextHolder.getContext();
                        sc.setAuthentication(authentication);
                        HttpSession session = req.getSession(true);
                        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
                    }


                    // Ajoutez des logs pour indiquer que l'authentification a réussi
                    log.info("Authentication successful for user: {}", user.getUsername());

                    // L'utilisateur est authentifié, continuez avec le reste du traitement
                    userConnecte = existingUser;
                    model.addAttribute("membreConnecte", userConnecte);
                    return "redirect:/";
                } catch (AuthenticationException e) {
                    log.warn("Authentication failed for user: {}", user.getUsername(), e);
                    // L'authentification a échoué, redirigez vers la page de connexion avec un message d'erreur
                    model.addAttribute("error", true);
                    return "login";
                }
            } else {
                model.addAttribute("error", true);
                return "login";
            }
        } else {
            return "redirect:/private/auth/register";
        }
    }

    //gestion du membre connecte:
    @ModelAttribute("membreConnecte")
    public User membreConnecte() {
        return userConnecte;
    }
}
