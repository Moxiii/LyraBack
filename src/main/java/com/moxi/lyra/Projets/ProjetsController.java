package com.moxi.lyra.Projets;


import com.moxi.lyra.Config.CustomAnnotation.RequireAuthorization;
import com.moxi.lyra.Config.Utils.SecurityUtils;
import com.moxi.lyra.DTO.CreateProjectDTO;
import com.moxi.lyra.DTO.ProjectRes;
import com.moxi.lyra.DTO.UpdateProjectDTO;
import com.moxi.lyra.User.User;
import com.moxi.lyra.User.UserService;
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
    private UserService userService;

    private ProjectRes toRes(Projets projets){
        ProjectRes pr = new ProjectRes();
        pr.setId(projets.getId());
        pr.setName(projets.getName());
        pr.setDescription(projets.getDescription());
        pr.setLinks(projets.getLinks());
        pr.setUsers(projets.getUsers().stream().map(User::getUsername).collect(Collectors.toList()));
        if(projets.getProjectPicture() != null){
            pr.setProjectPicture(projets.getProjectPicture());
        }else{
            pr.setProjectPicture(null);
        }
        return pr;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getMyProject() {
            User currentUser = SecurityUtils.getCurrentUser();
            List<Projets> projects  = projetsRepository.findByUsers(currentUser);
            List<ProjectRes> projectResponses = projects.stream().map(this::toRes).collect(Collectors.toList());
            return new ResponseEntity<>(projectResponses, HttpStatus.OK);
        }


    @PostMapping( "/add" )
    public ResponseEntity<?> createProject(@RequestBody CreateProjectDTO createProject){
            User currentUser = SecurityUtils.getCurrentUser();
            long nextProjectId = projetsRepository.findByUsers(currentUser).size() + System.currentTimeMillis();
            Projets projet = new Projets();
            projet.setId(Long.parseLong(currentUser.getId()+""+nextProjectId));
            projet.setName(createProject.getName());
            projet.setDescription(createProject.getDescription() != null ? createProject.getDescription() : "");
            projet.setLinks(createProject.getLinks() != null ? createProject.getLinks() : new ArrayList<>());
            List<User> users = new ArrayList<>();
            for (String username :createProject.getUsername()){
                User user = userService.findByUsername(username);
                users.add(user);
            }
            if(!users.contains(currentUser)){
                users.add(currentUser);
            }
            projet.setUsers(users);
            ProjectRes response = toRes(projet);
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



    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProject( @PathVariable long id, @RequestBody UpdateProjectDTO updateProject) {
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
                        User user = userService.findByUsername(usernames);
                        users.add(user);
                    }
                projet.setUsers(users);
                }
                ProjectRes response = this.toRes(projet);
                projetsRepository.save(projet);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not your Project !");
        }
    @PostMapping("/project/upload/picture/{id}")
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
}
