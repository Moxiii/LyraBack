package org.georges.georges.Auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Config.TokenManager;
import org.georges.georges.Response.LoginRes;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
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
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private JwtUtil jwtUtil;
@Autowired
private TokenManager tokenManager;
    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            UserRole defaultRole = new UserRole("user", "user", 1l);
            user.setUserRole(defaultRole);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = dateFormat.format(new Date());
            user.setDateInscription(formattedDate);
            userRepository.save(user);
            return new ResponseEntity<>("User creer avec sucess", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur dans la creation de l'user ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = {"/login"})
    @ResponseBody
    public ResponseEntity<?> inscriptionSubmit(@RequestBody User user, HttpServletRequest req) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Vérifier si l'utilisateur existe dans votre système

        User existingUser = null;
        if (user.getEmail() != null) {
            existingUser = userService.findByEmail(user.getEmail().toLowerCase());
        } else if (user.getUsername() != null) {
            existingUser = userService.findByUsername(user.getUsername().toLowerCase());
        }

        if (existingUser != null) {
            // L'utilisateur existe, vérifiez le mot de passe
            if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                String validToken = tokenManager.getValidToken(existingUser.getUsername());
                if (validToken != null) {
                    return ResponseEntity.ok(new LoginRes(existingUser.getUsername(), validToken));
                } else {
                    // Créez un token d'authentification
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(existingUser.getUsername(), user.getPassword());

                    try {
                        // Utilisez l'AuthenticationManager pour authentifier l'utilisateur
                        Authentication authentication = authenticationManager.authenticate(token);
                        if (authentication != null && authentication.isAuthenticated()) {
                            // L'utilisateur est authentifié, vous pouvez accéder aux détails d'authentification
                            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                            SecurityContext sc = SecurityContextHolder.getContext();
                            sc.setAuthentication(authentication);
                            HttpSession session = req.getSession(true);
                            String jwtToken = jwtUtil.createToken(user);
                            tokenManager.addToken(user.getUsername(), jwtToken);
                            // Retournez le token JWT dans la réponse
                            return ResponseEntity.ok(new LoginRes(existingUser.getUsername(), jwtToken));
                        }
                    } catch (AuthenticationException e) {
                        log.warn("Authentication failed for user: {}", user.getUsername(), e);
                        // L'authentification a échoué, retournez une erreur dans la réponse
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
                    }
                }
            } else {
                // Le mot de passe est incorrect, retournez une erreur dans la réponse
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
            }
        } else {
            // L'utilisateur n'existe pas, retournez une erreur dans la réponse
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }
}


