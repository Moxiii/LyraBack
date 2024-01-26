package org.georges.georges.pojos;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;

import java.util.List;

@Entity
public class UserRole {
    @OneToMany(mappedBy = "userRole" , fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<User> userList;
    @NotBlank(message = "Le champ role n'est pas renseigné")
    private String role;
    @NotBlank(message = "Le champ description n'est pas renseigné")
    private String description;
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public UserRole(String role, String description, Long id) {
        this.role = role;
        this.description = description;
        this.id = id;
    }
    public UserRole(){}

    public String getNom() {
        return role;
    }

    public void setNom(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

}
