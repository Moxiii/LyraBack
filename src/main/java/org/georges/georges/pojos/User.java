package org.georges.georges.pojos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@Entity
public class User {
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private UserRole userRole;
    @NotBlank(message = "Le champ nom n'est pas renseigné")
    private String name;
    @NotBlank(message = "Le champ pseudo n'est pas renseigné")
    private String pseudo;
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

    public User(String name, String pseudo, String email, String password, String dateInscription, UserRole userRole) {
        this.name = name;
        this.pseudo = pseudo;
        this.email = email;
        this.password = passwordEncoder.encode(password);
        this.dateInscription = dateInscription;
        this.userRole = userRole;
    }

public User(){}

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = Long.valueOf(id);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDateInscription(String dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDateInscription() {
        return dateInscription;
    }
}

