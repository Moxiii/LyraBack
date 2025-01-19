package org.georges.georges.Calendar.event;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.georges.georges.Calendar.Calendar;
import org.georges.georges.Calendar.RecurrenceRule;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate startHours;
    private LocalDate endHours;
    private boolean completed;
    @ElementCollection
    private Set<String> tags;
    private RecurrenceRule recurrenceRule;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;
}
