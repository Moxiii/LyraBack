package com.moxi.lyra.Contact;

import com.moxi.lyra.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ContactRepository extends JpaRepository<Contact, Long > {

@Query("SELECT c FROM Contact c WHERE (c.user.id = :userID AND c.contact.id = :contactUserID) OR (c.user.id = :contactUserID AND c.contact.id = :userID)")
Optional<Contact> findContactBetweenUsers(@Param("userID") Long userID, @Param("contactUserID") Long contactUserID);


    List<Contact> findAllByUser(User user);
}


