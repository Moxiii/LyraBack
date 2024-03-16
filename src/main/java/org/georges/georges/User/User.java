package org.georges.georges.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.georges.georges.Conversation.Conversation;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "user")
    private List<UserQueue> queues;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User(String name, String username, String email, String password, String dateInscription, UserRole userRole) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = passwordEncoder.encode(password);
        this.dateInscription = dateInscription;
        this.userRole = userRole;
    }

public User(){

}

}

