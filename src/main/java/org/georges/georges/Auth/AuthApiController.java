package org.georges.georges.Auth;


import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Config.TokenManager;
import org.georges.georges.Response.LoginRes;
import org.georges.georges.User.Provider;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
import org.georges.georges.User.UserRole.UserRole;
import org.georges.georges.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Collections;
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


    private static final String CLIENT_ID = "1047364862184-5ht68ioumb1cnjmpivurtmvtnb7fkr4s.apps.googleusercontent.com\n";
    private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

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
                        Authentication authentication = authenticationManager.authenticate(token);
                        if (authentication != null && authentication.isAuthenticated()) {
                            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                            SecurityContext sc = SecurityContextHolder.getContext();
                            sc.setAuthentication(authentication);
                            HttpSession session = req.getSession(true);
                            String accessToken = jwtUtil.createAccessToken(user);
                            String refreshToken = jwtUtil.createRefreshToken(user);
                            tokenManager.addToken(user.getUsername() + "_refresh",refreshToken);
                            tokenManager.addToken(user.getUsername(), accessToken);
                            return ResponseEntity.ok(new LoginRes(existingUser.getUsername(), accessToken));
                        }
                    } catch (AuthenticationException e) {
                        log.warn("Authentication failed for user: {}", user.getUsername(), e);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
                    }
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }

@DeleteMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest req) {
        String token = jwtUtil.extractTokenFromRequest(req);
        if (token != null) {
            String username = jwtUtil.extractUsername(token);
            if (username != null) {
                tokenManager.removeToken(username);
                return ResponseEntity.status(HttpStatus.OK).body("User logged out successfully");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
}
@PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest req) {
        String token = jwtUtil.extractTokenFromRequest(req);
        String newAccessToken = jwtUtil.checkToken(req);
        if (newAccessToken != null) {
            String username = jwtUtil.extractUsername(newAccessToken);
            return ResponseEntity.ok(new LoginRes(username, newAccessToken));
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
}


}


