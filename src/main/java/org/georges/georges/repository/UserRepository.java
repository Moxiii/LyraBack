package org.georges.georges.repository;

import org.georges.georges.pojos.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    boolean existsByUsername(String pseudo);
    void deleteByEmail(String Email );

    boolean existsByEmail(String email);

    List<User> findUserByUsername(String username);
    List<User>findUserByEmail(String email);

    User findByUsername(String username);

    List<User> findByUsernameContainingOrEmailContaining(String username , String  email);

    User findByEmail(String email);
}