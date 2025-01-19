package org.georges.georges.Projets;


import org.georges.georges.Config.CustomAnnotation.RequireAuthorization;
import org.georges.georges.Config.Utils.SecurityUtils;
import org.georges.georges.DTO.CreateProjectDTO;
import org.georges.georges.DTO.ProjectRes;
import org.georges.georges.DTO.UpdateProjectDTO;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequireAuthorization
@RestController
@RequestMapping("/api/project")
public class ProjetsController {
    @Autowired
    private ProjetsRepository projetsRepository;
    @Autowired
    private UserRepository userRepository;

    private ProjectRes toRes(Projets projets){
        ProjectRes pr = new ProjectRes();
        pr.setId(projets.getId());
        pr.setName(projets.getName());
        pr.setDescription(projets.getDescription());
        pr.setLinks(projets.getLinks());
        pr.setUsers(projets.getUsers().stream().map(User::getUsername).collect(Collectors.toList()));
        return pr;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getMyProject() {
            User currentUser = SecurityUtils.getCurrentUser();
            List<Projets> projets  = projetsRepository.findByUsers(currentUser);
            List<ProjectRes> projectResponses = projets.stream().map(this::toRes).collect(Collectors.toList());
            return new ResponseEntity<>(projectResponses, HttpStatus.OK);
        }


    @PostMapping("/add")
    public ResponseEntity<?> createProject(@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody CreateProjectDTO createProject) {
            User currentUser = SecurityUtils.getCurrentUser();
            long nextProjectId = projetsRepository.findByUsers(currentUser).size() + System.currentTimeMillis();
            Projets projet = new Projets();
            projet.setId(Long.parseLong(currentUser.getId()+""+nextProjectId));
            projet.setName(createProject.getName());
            projet.setDescription(createProject.getDescription() != null ? createProject.getDescription() : "");
            projet.setLinks(createProject.getLinks() != null ? createProject.getLinks() : new ArrayList<>());
            List<User> users = new ArrayList<>();
            for (String username :createProject.getUsername()){
                User user = userRepository.findByUsername(username);
                users.add(user);
            }
            if(!users.contains(currentUser)){
                users.add(currentUser);
            }
            projet.setUsers(users);
            ProjectRes response = toRes(projet);
            if (file != null && !file.isEmpty()) {
                try {
                    projet.setProjectPicture(file.getBytes());
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi de l'image");
                }
            }
            projetsRepository.save(projet);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable long id) {
            User currentUser = SecurityUtils.getCurrentUser();
            Projets projet = projetsRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
            if(!projet.getUsers().contains(currentUser)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
            }
            projetsRepository.delete(projet);
            return ResponseEntity.status(HttpStatus.OK).body("Project deleted");
    }

    @PostMapping("/upload/projectPic/{id}")
    public ResponseEntity<?> uploadProjectPic(
            @RequestParam("file") MultipartFile file,
            @PathVariable long id) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Aucun fichier reçu");
        }
            User currentUser = SecurityUtils.getCurrentUser();

            try {
                Projets projet = projetsRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Projet introuvable"));

                boolean userIsInProject = projet.getUsers().stream()
                        .anyMatch(user -> user.getId().equals(currentUser.getId()));

                if (!userIsInProject) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Vous n'avez pas les droits pour modifier ce projet");
                }
                projet.setProjectPicture(file.getBytes());
                projetsRepository.save(projet);

                return ResponseEntity.ok("Image du projet mise à jour avec succès");
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors de l'envoi de l'image");
            }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProject( @PathVariable long id , @RequestParam(value = "file", required = false) MultipartFile file, @RequestBody UpdateProjectDTO updateProject) {
            User currentUser = SecurityUtils.getCurrentUser();
            Projets projet = projetsRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
            boolean userIsInProject = projet.getUsers().stream()
                    .anyMatch(user -> user.getId().equals(currentUser.getId()));
            if(userIsInProject) {
              projet.setName(updateProject.getName() != null ? updateProject.getName() : projet.getName());
                projet.setDescription(updateProject.getDescription() != null ? updateProject.getDescription() : projet.getDescription());
                projet.setLinks(updateProject.getLinks() != null ? updateProject.getLinks() : projet.getLinks());
                if(updateProject.getUsername() != null && !updateProject.getUsername().isEmpty() ){
                    List<User> users = new ArrayList<>();
                    for(String usernames : updateProject.getUsername()){
                        User user = userRepository.findByUsername(usernames);
                        users.add(user);
                    }
                projet.setUsers(users);
                }
                if (file != null && !file.isEmpty()) {
                    try {
                        projet.setProjectPicture(file.getBytes());
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi de l'image");
                    }
                }
                ProjectRes response = this.toRes(projet);
                projetsRepository.save(projet);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not your Project !");
        }
}
