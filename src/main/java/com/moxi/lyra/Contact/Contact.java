package com.moxi.lyra.Contact;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.moxi.lyra.User.User;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name="contact_user_id")
    private User contact;
    private ContactStatus status;
    private LocalDate dateAdded;
}
