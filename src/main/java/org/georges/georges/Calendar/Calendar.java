package org.georges.georges.Calendar;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.georges.georges.User.User;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
public class Calendar {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean completed;
    @ManyToOne
    private User user;
    @ElementCollection
    private Set<String> tags;
    private ReccurenceRule recurrenceRule;

    public Calendar() {

    }
}
