package org.georges.georges.Auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRole.UserRepository;
import org.georges.georges.User.UserRole.UserRole;
import org.georges.georges.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
@Slf4j
@RestController
@RequestMapping("api/auth")
public class AuthApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody User user){
        try{
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            UserRole defaultRole = new UserRole("user","user",1l);
            user.setUserRole(defaultRole);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = dateFormat.format(new Date());
            user.setDateInscription(formattedDate);
            userRepository.save(user);
            return new ResponseEntity<>("User creer avec sucess"  , HttpStatus.CREATED);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Erreur dans la creation de l'user "  , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Vérifier si l'utilisateur existe dans votre système
        log.info("Attempting to authenticate user: {}", user.getEmail());
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
                        HttpSession session = request.getSession(true);
                        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
                    }

                    // Ajoutez des logs pour indiquer que l'authentification a réussi
                    log.info("Authentication successful for user: {}", existingUser.getUsername());

                    // L'utilisateur est authentifié
                    return ResponseEntity.ok("User authenticated successfully.");
                } catch (AuthenticationException e) {
                    log.warn("Authentication failed for user: {}", user.getEmail(), e);
                    // L'authentification a échoué
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed.");
                }
            } else {
                // Mot de passe incorrect
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password.");
            }
        } else {
            // Utilisateur non trouvé
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}
