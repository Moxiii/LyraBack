package com.moxi.lyra.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.moxi.lyra.Contact.Contact;
import com.moxi.lyra.Conversation.Conversation;
import com.moxi.lyra.Todo.Todo;
import com.moxi.lyra.Calendar.Calendar;
import com.moxi.lyra.User.UserRole.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
public class User {
    @JsonIgnore
    @ManyToMany(mappedBy = "participants")
    private Set<Conversation> conversations;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private UserRole userRole;
    private String name;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String dateInscription;
    private String Description;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Provider provider;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private List<Todo> todos;
    @OneToOne(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Calendar calendar;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 500*1024*1024)
    private byte[] profilePicture;
    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User(String name, String username, String email, String password, String dateInscription, UserRole userRole) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = passwordEncoder.encode(password);
        this.dateInscription = dateInscription;
        this.userRole = userRole;
    }

    public User(String name, String email, String dateInscription) {
        this.name = name;
        this.email = email;
        this.dateInscription = dateInscription;
    }

    public User(){

}

}

