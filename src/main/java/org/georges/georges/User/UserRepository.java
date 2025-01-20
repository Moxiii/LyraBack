package org.georges.georges.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByUsernameContainingOrEmailContaining(String username , String  email);

    User findByEmail(String email);

}