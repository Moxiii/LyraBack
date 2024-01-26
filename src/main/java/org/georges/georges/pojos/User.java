package org.georges.georges.pojos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
public class User {
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private UserRole userRole;
    @NotBlank(message = "Le champ nom n'est pas renseigné")
    private String nom;
    @NotBlank(message = "Le champ pseudo n'est pas renseigné")
    private String pseudo;
    @NotBlank(message = "Le champ email n'est pas renseigné")
    @Email(message = "L'email n'est pas valide")
    private String email;
    @NotBlank(message = "Le champ password n'est pas renseigné")
    @Size(min = 6, message = "Le password doit avoir au moins 6 caractères")
    private String password;
    private Date dateInscription;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User(String nom, String pseudo, String email, String password, Date dateInscription, UserRole userRole) {
        this.nom = nom;
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


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public void setDateInscription(Date dateInscription) {
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

    public Date getDateInscription() {
        return dateInscription;
    }
}
