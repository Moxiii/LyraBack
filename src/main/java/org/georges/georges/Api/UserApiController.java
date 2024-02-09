package org.georges.georges.Api;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRole.UserRepository;
import org.georges.georges.User.UserRole.UserRole;
import org.georges.georges.User.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
@Slf4j
@RequestMapping("api/user")
@RestController
public class UserApiController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getAll")
    public List<User> getAllUsers() {
        List<User> allUser = userRepository.findAll();
        return allUser;
    }
@GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        User user = userRepository.findById(id).orElse(null);
        if (user != null){
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.notFound().build();
        }
}
@PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id , @RequestBody User userToUpdate) {
    User existingUser = userRepository.findById(id).orElse(null);
    if (existingUser != null) {
        existingUser.setName(userToUpdate.getName());
        //existingUser.setUserRole(userToUpdate.getUserRole());
        userRepository.save(existingUser);
        return new ResponseEntity<>("User mis à jour avec succès", HttpStatus.OK);
    } else {
        return new ResponseEntity<>("User non trouvé", HttpStatus.NOT_FOUND);
    }
}
@DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return new ResponseEntity<>("Avis supprimé avec succès", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de la suppression de l'avis", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}

