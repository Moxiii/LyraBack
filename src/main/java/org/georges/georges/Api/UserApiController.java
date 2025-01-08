package org.georges.georges.Api;

import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
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


@PutMapping("/update/")
    public ResponseEntity<String> updateUser( HttpServletRequest request, @RequestBody User userToUpdate) {
    if(SecurityUtils.isAuthorized(request, jwtUtil)){
        User currentUser = SecurityUtils.getCurrentUser();
        currentUser.setName(userToUpdate.getName());
        currentUser.setEmail(userToUpdate.getEmail());
        currentUser.setPassword(userToUpdate.getPassword());
        currentUser.setProfilePicture(userToUpdate.getProfilePicture());
        currentUser.setUsername(userToUpdate.getUsername());
        userRepository.save(currentUser);
        return new ResponseEntity<>("User mis à jour avec succès", HttpStatus.OK);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
}
@DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser( HttpServletRequest request) {
    if(SecurityUtils.isAuthorized(request, jwtUtil)){
        User currentUser = SecurityUtils.getCurrentUser();
        try {
            userService.deleteUserById(currentUser.getId());
            return new ResponseEntity<>("User supprimé avec succès", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de la suppression de l'user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
                if(currentUser.getDescription() == null){currentUser.setDescription("basic user of Gilbert");}
                UserProfileRes profileRes = new UserProfileRes(
                        currentUser.getName(),
                        currentUser.getDescription(),
                        currentUser.getEmail()
                );
                if(currentUser.getProfilePicture() != null){profileRes.setProfileImage(currentUser.getProfilePicture());}
                return new ResponseEntity<>(profileRes, HttpStatus.OK);
            }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorRes(HttpStatus.UNAUTHORIZED, "User not found"));
    }
    @PostMapping("/upload/profilPic")
    public ResponseEntity<?> uploadProfilPic(@RequestParam("file") MultipartFile file , HttpServletRequest request) {
        if (file.isEmpty()) {
            log.warn("Aucun fichier reçu !");
            return ResponseEntity.badRequest().body("Aucun fichier reçu");
        }
        log.info("Nom du fichier reçu : " + file.getOriginalFilename());
        log.info("Taille du fichier : " + file.getSize());
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
            try{
                byte[] imageBytes = file.getBytes();
                currentUser.setProfilePicture(imageBytes);
                userRepository.save(currentUser);
                UserProfileRes userProfileRes = new UserProfileRes();
                userProfileRes.setProfileImage(currentUser.getProfilePicture());
                userProfileRes.setUsername(currentUser.getUsername());
                userProfileRes.setEmailAddress(currentUser.getEmail());
                return new ResponseEntity<>(userProfileRes, HttpStatus.OK);
            }
            catch (IOException e){return ResponseEntity.status(500).body("Error uploading image.");}

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    @GetMapping("/get/profilPic")
    public ResponseEntity<?> getProfilPic(HttpServletRequest request) {
    if(SecurityUtils.isAuthorized(request, jwtUtil)){
        User currentUser = SecurityUtils.getCurrentUser();
        byte[] imageBytes = currentUser.getProfilePicture();
        if(imageBytes == null || imageBytes.length == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return  new ResponseEntity<>( imageBytes, HttpStatus.OK);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
}

