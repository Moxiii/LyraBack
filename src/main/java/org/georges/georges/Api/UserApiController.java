package org.georges.georges.Api;

import org.georges.georges.Config.CustomAnnotation.RequireAuthorization;
import org.georges.georges.Config.Utils.SecurityUtils;
import org.georges.georges.DTO.UserProfileRes;
import org.georges.georges.User.User;
import org.georges.georges.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequireAuthorization
@RequestMapping("api/user")
@RestController
public class UserApiController {

    @Autowired
    private UserService userService;



@PutMapping("/update/")
    public ResponseEntity<String> updateUser(  @RequestBody User userToUpdate) {
        User currentUser = SecurityUtils.getCurrentUser();
        currentUser.setName(userToUpdate.getName());
        currentUser.setEmail(userToUpdate.getEmail());
        currentUser.setPassword(userToUpdate.getPassword());
        currentUser.setProfilePicture(userToUpdate.getProfilePicture());
        currentUser.setUsername(userToUpdate.getUsername());
        userService.saveUser(currentUser);
        return new ResponseEntity<>("User mis à jour avec succès", HttpStatus.OK);
}
@DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser( ) {
        User currentUser = SecurityUtils.getCurrentUser();
        try {
            userService.deleteUser(currentUser);
            return new ResponseEntity<>("User supprimé avec succès", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de la suppression de l'user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile() {
            User currentUser = SecurityUtils.getCurrentUser();
                if(currentUser.getDescription() == null){currentUser.setDescription("basic user of Gilbert");}
                UserProfileRes profileRes = new UserProfileRes();
                profileRes.setUsername(currentUser.getUsername());
                profileRes.setDescription(currentUser.getDescription());
                profileRes.setEmailAddress(currentUser.getEmail());
                if(currentUser.getProfilePicture() != null){profileRes.setProfileImage(currentUser.getProfilePicture());}
                return new ResponseEntity<>(profileRes, HttpStatus.OK);
    }
    @PostMapping("/upload/profil/picture")
    public ResponseEntity<?> uploadProfilPic(@RequestParam("file") MultipartFile file ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucun fichier reçu");
        }
            User currentUser = SecurityUtils.getCurrentUser();
            try{
                byte[] imageBytes = file.getBytes();
                currentUser.setProfilePicture(imageBytes);
                userService.saveUser(currentUser);
                UserProfileRes userProfileRes = new UserProfileRes();
                userProfileRes.setProfileImage(currentUser.getProfilePicture());
                userProfileRes.setUsername(currentUser.getUsername());
                return new ResponseEntity<>(userProfileRes, HttpStatus.OK);
            }
            catch (IOException e){return ResponseEntity.status(500).body("Error uploading image.");}

    }
}

