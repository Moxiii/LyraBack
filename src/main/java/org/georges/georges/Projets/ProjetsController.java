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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
            Optional<Projets> projet = projetsRepository.findById(id);
            if(projet.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
            }
            Projets projets = projet.get();
            boolean userIsInProject = projets.getUsers().stream()
                    .anyMatch(user -> user.getId().equals(currentUser.getId()));
            if(userIsInProject) {
                projets.setName(updateProject.getName());
                projets.setDescription(updateProject.getDescription());
                projets.setLinks(updateProject.getLinks());
                projets.setUsers(updateProject.getUsers());
                projetsRepository.save(projets);
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
            Optional<Projets> projet = projetsRepository.findById(id);
            if(projet.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
            }
            if(!projet.get().getUsers().contains(currentUser)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
            }
            projetsRepository.delete(projet.get());
            return ResponseEntity.status(HttpStatus.OK).body("Project deleted");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
}
