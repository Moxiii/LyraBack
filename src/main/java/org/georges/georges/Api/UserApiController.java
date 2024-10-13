package org.georges.georges.Api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Config.SecurityUtils;
import org.georges.georges.Response.ErrorRes;
import org.georges.georges.Response.UserProfileRes;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
import org.georges.georges.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("api/user")
@RestController
public class UserApiController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

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
            return new ResponseEntity<>("User supprimé avec succès", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de la suppression de l'user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//@GetMapping("/me")
//    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
//        boolean isAuth = SecurityUtils.isAuthorized(request , jwtUtil);
//        if (isAuth == true) {
//                User currentUser = SecurityUtils.getCurrentUser();
//                if (currentUser != null) {
//                    if (currentUser.getDescription() == null) {
//                        currentUser.setDescription("Basic User of Gilbert");
//                    }
//                    UserProfileRes profileRes = new UserProfileRes(
//                            currentUser.getUsername(),
//                            currentUser.getEmail(),
//                            currentUser.getDescription()
//                    );
//                    return new ResponseEntity<>(profileRes, HttpStatus.OK);
//                }
//            }
//    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorRes(HttpStatus.UNAUTHORIZED, "User not found"));
//}
    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            User currentUser = userRepository.findByUsername(username);
            if (currentUser != null) {
                if(currentUser.getDescription() == null){currentUser.setDescription("basic user of Gilbert");}
                UserProfileRes profileRes = new UserProfileRes(
                        currentUser.getUsername(),
                        currentUser.getDescription(),
                        currentUser.getEmail()
                );
                return new ResponseEntity<>(profileRes, HttpStatus.OK);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorRes(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}

