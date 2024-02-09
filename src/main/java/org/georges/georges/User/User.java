package org.georges.georges.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.georges.georges.Conversation.Conversation;
import org.georges.georges.User.UserRole.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
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
    @NotBlank(message = "Le champ nom n'est pas renseigné")
    private String name;
    @NotBlank(message = "Le champ Username n'est pas renseigné")
    private String username;
    @NotBlank(message = "Le champ email n'est pas renseigné")
    @Email(message = "L'email n'est pas valide")
    private String email;
    @NotBlank(message = "Le champ password n'est pas renseigné")
    @Size(min = 6, message = "Le password doit avoir au moins 6 caractères")
    private String password;
    private String dateInscription;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User(String name, String username, String email, String password, String dateInscription, UserRole userRole) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = passwordEncoder.encode(password);
        this.dateInscription = dateInscription;
        this.userRole = userRole;
    }

public User(){}

    public User(String username, String mail, String rawPassword) {
        this.username = username;
        this.email = mail;
        this.password = rawPassword;
    }

}

