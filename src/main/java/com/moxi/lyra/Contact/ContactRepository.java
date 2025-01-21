package com.moxi.lyra.Contact;

import com.moxi.lyra.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ContactRepository extends JpaRepository<Contact, Long > {
    @Query("SELECT c FROM Contact c WHERE c.contact = :user ")
    Contact findByUser(@Param("user")User user);

    @Query("SELECT c FROM Contact c WHERE c.contact.id = :contactUserId AND c.user.id =:userId")
    Contact findContactById(@Param("contactUserId")Long contactUserId , @Param("userId") Long userId);


    List<Contact> findAllByUser(User user);
}


