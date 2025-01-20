package org.georges.georges.Calendar.event;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.georges.georges.Calendar.Calendar;
import org.georges.georges.Calendar.RecurrenceRule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private LocalTime startHours;
    private LocalTime endHours;
    private boolean completed = false;
    @ElementCollection
    private Set<String> tags;
    private RecurrenceRule recurrenceRule;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;


}
