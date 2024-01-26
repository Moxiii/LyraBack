package org.georges.georges.repository;

import org.georges.georges.pojos.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByPseudo(String pseudo);
    boolean existsByPseudo(String pseudo);
    void deleteByEmail(String Email );

}