package org.georges.georges.pojos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;

public class User {
    @NotBlank(message = "Le champ nom n'est pas renseigné")
    private final String nom;
    @NotBlank(message = "Le champ pseudo n'est pas renseigné")
    private final String pseudo;
    @NotBlank(message = "Le champ email n'est pas renseigné")
    @Email(message = "L'email n'est pas valide")
    private final String email;
    @NotBlank(message = "Le champ password n'est pas renseigné")
    @Size(min = 6, message = "Le password doit avoir au moins 6 caractères")
    private final String password;
    private final Date dateInscription;
    private int id;

    public User(String nom, String pseudo, String email, String password, Date dateInscription) {
        this.nom = nom;
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.dateInscription = dateInscription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
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
