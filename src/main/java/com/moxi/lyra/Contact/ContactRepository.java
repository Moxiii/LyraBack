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

    @Query("SELECT c FROM Contact c WHERE c.contact.id = :contactUserID AND c.user.id =:userID")
    Optional<Contact> findContactById(@Param("contactUserID")Long contactUserId , @Param("userID") Long userID);


    List<Contact> findAllByUser(User user);
}


