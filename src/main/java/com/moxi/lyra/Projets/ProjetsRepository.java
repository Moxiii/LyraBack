package com.moxi.lyra.Projets;


import com.moxi.lyra.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetsRepository extends JpaRepository <Projets, Long> {
    List<Projets> findByUsers(User user);
}
