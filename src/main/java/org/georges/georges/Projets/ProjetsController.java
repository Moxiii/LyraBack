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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
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

    @RequestMapping("/get")
    public ResponseEntity<?> getMyProject(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
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
}
