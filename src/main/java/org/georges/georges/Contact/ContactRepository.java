package org.georges.georges.Contact;

import org.georges.georges.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ContactRepository extends JpaRepository<Contact, Long > {
    List<Contact> findAllByUser(User user);
    Contact findByUser(User user);
}


