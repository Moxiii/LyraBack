package org.georges.georges.Projets;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Config.SecurityUtils;
import org.georges.georges.Response.ProjectRes;
import org.georges.georges.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/project")
public class ProjetsController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ProjetsRepository projetsRepository;

    @GetMapping("/get")
    public ResponseEntity<?> getMyProject(HttpServletRequest request) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
            List<Projets> projets  = projetsRepository.findByUsers(currentUser);
            List<ProjectRes> projectResponses = projets.stream().map(projet -> {
                ProjectRes projectRes = new ProjectRes();
                projectRes.setId(projet.getId());
                projectRes.setName(projet.getName());
                projectRes.setDescription(projet.getDescription());
                projectRes.setLinks(projet.getLinks());
                List<String> usernames = projet.getUsers().stream()
                        .map(User::getUsername)
                        .collect(Collectors.toList());
                projectRes.setUsers(usernames);
                return projectRes;
            }).collect(Collectors.toList());
            return new ResponseEntity<>(projectResponses, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProject(HttpServletRequest request, @PathVariable long id , @RequestBody Projets updateProject) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
            Projets projet = projetsRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
            boolean userIsInProject = projet.getUsers().stream()
                    .anyMatch(user -> user.getId().equals(currentUser.getId()));
            if(userIsInProject) {
                projet.setName(updateProject.getName());
                projet.setDescription(updateProject.getDescription());
                projet.setLinks(updateProject.getLinks());
                projet.setUsers(updateProject.getUsers());
                projetsRepository.save(projet);
                return ResponseEntity.status(HttpStatus.OK).body("Project updated");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not your Project !");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @PostMapping("/add")
    public ResponseEntity<?> createProject(HttpServletRequest request , @RequestBody Projets createProject) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
            long nextProjectId = projetsRepository.findByUsers(currentUser).size() + System.currentTimeMillis();
            Projets projet = new Projets();
            projet.setId(Long.parseLong(currentUser.getId()+""+nextProjectId));
            projet.setName(createProject.getName());
            projet.setDescription(createProject.getDescription());
            projet.setLinks(createProject.getLinks());
            projet.setUsers(createProject.getUsers());
            projetsRepository.save(projet);
            return ResponseEntity.status(HttpStatus.CREATED).body("Project created");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProject(HttpServletRequest request, @PathVariable long id) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
            Projets projet = projetsRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
            if(!projet.getUsers().contains(currentUser)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
            }
            projetsRepository.delete(projet);
            return ResponseEntity.status(HttpStatus.OK).body("Project deleted");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @PostMapping("/upload/projectPic/{id}")
    public ResponseEntity<?> uploadProjectPic(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request,
            @PathVariable long id) {
        if (file.isEmpty()) {
            log.warn("Aucun fichier reçu !");
            return ResponseEntity.badRequest().body("Aucun fichier reçu");
        }

        if (SecurityUtils.isAuthorized(request, jwtUtil)) {
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
                log.error("Erreur lors du téléchargement de l'image", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors de l'envoi de l'image");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
