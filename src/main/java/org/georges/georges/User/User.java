package org.georges.georges.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.georges.georges.Contact.Contact;
import org.georges.georges.Conversation.Conversation;
import org.georges.georges.Todo.Todo;
import org.georges.georges.User.UserRole.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
public class User {
    @ManyToMany(mappedBy = "participants")
    private Set<Conversation> conversations;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private UserRole userRole;
    private String name;
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

