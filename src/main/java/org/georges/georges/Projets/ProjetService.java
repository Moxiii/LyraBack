package org.georges.georges.Projets;

import org.georges.georges.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjetService {
    @Autowired
    private ProjetsRepository projetsRepository;
    public List<Projets> findByUser(User user) {
         return  projetsRepository.findByUsers(user);
    }
    public void deleteAllByUser(User user) {
        List<Projets> userInProject = projetsRepository.findByUsers(user);
        for (Projets projet : userInProject) {
            userInProject.remove(projet);
            if(userInProject.isEmpty()) {
                projetsRepository.delete(projet);
            }
            projetsRepository.save(projet);
        }
    }
}
