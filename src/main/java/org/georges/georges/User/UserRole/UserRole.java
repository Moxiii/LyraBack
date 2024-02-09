package org.georges.georges.User.UserRole;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.georges.georges.User.User;
import org.springframework.data.annotation.Id;

import java.util.List;
@Getter
@Setter
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


    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

}
