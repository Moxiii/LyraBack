package org.georges.georges.pojos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
public class User {

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

    @ColumnDefault(value = "'ROLE_USER'")
    @ElementCollection
    private List<String> roles;

    public User(String nom, String pseudo, String email, String password, Date dateInscription, List<String> roles) {
        this.nom = nom;
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.dateInscription = dateInscription;
        this.roles = roles;
    }

// todo add role for conversation 
    public User() {

    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
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

    //auto exec role injection :
    @PrePersist
    public void prePersist() {
        if (roles == null || roles.isEmpty()) {
            roles = Arrays.asList("ROLE_USER");
        }
    }
}
