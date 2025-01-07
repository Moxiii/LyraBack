package org.georges.georges.Projets;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.georges.georges.User.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Projets {
    @Id
    private Long id;
    private String name;
    private String description;
    @ElementCollection
    private List<String> links = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "projet_users",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();


    public Projets(String name, String description, List<String> links, List<User> users) {
        this.name = name;
        this.description = description;
        this.links = links;
        this.users = users;
    }

    public Projets() {

    }
    //TODO faire en sorte d'integerer des délais type deadline , faire un suivis des sprint etc etc + faire un modal pour update les projets eventuellement ou
    // faire en sorte que les projets est leurs Screen avec les discutions lier etc
}
