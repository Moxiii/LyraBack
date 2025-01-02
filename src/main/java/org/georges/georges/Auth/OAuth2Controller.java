package org.georges.georges.Auth;

import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Response.LoginRes;
import org.georges.georges.User.Provider;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
import org.georges.georges.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class OAuth2Controller {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/login/oauth2/google")
    public ResponseEntity<?> googleUser(Principal principal) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
        OAuth2User user = token.getPrincipal();

        String email = user.getAttribute("email");
        String firstName = user.getAttribute("given_name");
        String lastName = user.getAttribute("family_name");

        // Vérifie si l'utilisateur existe déjà dans la base de données
        User existingUser = userService.findByEmail(email);

        if (existingUser == null) {
            // Si l'utilisateur n'existe pas, on le crée
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(firstName);
            newUser.setUsername(lastName);
            newUser.setProvider(Provider.GOOGLE);
            userRepository.save(newUser);

            // Crée un token JWT pour ce nouvel utilisateur
            String jwtToken = jwtUtil.createAccessToken(newUser);

            // Retourne le token JWT
            return ResponseEntity.ok(new LoginRes(newUser.getUsername(), jwtToken));
        } else {
            // Si l'utilisateur existe déjà, et qu'il n'a pas de mot de passe local, il ne peut pas se connecter localement
            if (existingUser.getProvider() == Provider.GOOGLE && existingUser.getPassword() == null) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Connexion avec un mot de passe local interdite pour ce compte.");
            }

            // Si l'utilisateur a déjà un mot de passe local ou que le compte est local, on lui permet de se connecter.
            String jwtToken = jwtUtil.createAccessToken(existingUser);
            return ResponseEntity.ok(new LoginRes(existingUser.getUsername(), jwtToken));
        }
    }
}
