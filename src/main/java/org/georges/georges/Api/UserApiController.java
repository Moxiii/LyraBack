package org.georges.georges.Api;

import org.georges.georges.pojos.User;
import org.georges.georges.repository.UserRepository;
import org.georges.georges.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("api/user")
@RestController
public class UserApiController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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

 @PostMapping("/create")
 public ResponseEntity<String> createUser(@RequestBody User user){
        try{
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            return new ResponseEntity<>("User creer avec sucess"  , HttpStatus.CREATED);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Erreur dans la creation de l'user "  , HttpStatus.INTERNAL_SERVER_ERROR);
        }
 }
}

