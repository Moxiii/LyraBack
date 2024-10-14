package org.georges.georges.Projets;


import org.georges.georges.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetsRepository extends JpaRepository <Projets, Long> {
    List<Projets> findByName(String name);
    List<Projets> findByUsers(User user);
}
