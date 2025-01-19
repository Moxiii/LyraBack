package org.georges.georges.Auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.DTO.LoginRes;
import org.georges.georges.User.Provider;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
import org.georges.georges.User.UserRole.UserRole;
import org.georges.georges.User.UserRole.UserRoleRepository;
import org.georges.georges.User.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

@RestController
@RequestMapping("api/oauth2")
public class OAuth2Controller {
    private static final Logger log = LoggerFactory.getLogger(OAuth2Controller.class);
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    private static final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    @Autowired
    private UserRoleRepository userRoleRepository;

    @PostMapping("/google")
    public ResponseEntity<?> googleUser(@RequestBody TokenRequest tokenRequest) {
        String googleToken = tokenRequest.getToken();
        GoogleIdTokenVerifier verifier;
        try {
            verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory)
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            GoogleIdToken idToken = verifier.verify(googleToken);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String username = payload.getSubject();
                String name = (String) payload.get("name");
                String picture = (String) payload.get("picture");
                User existingUser = userService.findByEmail(email);
                Date aujourdhui = new Date();
                SimpleDateFormat formatedDate = new SimpleDateFormat("dd-MM-yyyy");
                String dateString = formatedDate.format(aujourdhui);
                UserRole userRole = userRoleRepository.findById(1l).get();
                if (existingUser == null) {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setUsername(username);
                    newUser.setProvider(Provider.GOOGLE);
                    newUser.setDateInscription(dateString);
                    newUser.setUserRole(userRole);
                    userRepository.save(newUser);
                    String jwtToken = jwtUtil.createAccessToken(newUser);
                    log.info("JWT token: " + jwtToken);
                    log.info("le nouveau utilisateur est : "  + newUser.getUsername());
                    return ResponseEntity.ok(new LoginRes(newUser.getUsername(), jwtToken));
                } else {
                    String jwtToken = jwtUtil.createAccessToken(existingUser);
                    return ResponseEntity.ok(new LoginRes(existingUser.getUsername(), jwtToken));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Google token.");
            }
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying Google token.");
        }
    }
}
