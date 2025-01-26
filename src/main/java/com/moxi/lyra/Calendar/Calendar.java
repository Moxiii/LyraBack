package com.moxi.lyra.Calendar;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.moxi.lyra.Calendar.Event.Event;
import com.moxi.lyra.User.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;


@Entity
@Getter
@Setter
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name="user_id" , insertable=true, updatable=true)
    @JsonBackReference
    private User user;
    @OneToMany(mappedBy = "calendar" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Event> events;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private List<DayOfWeek> workDays = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
    public Calendar() {

    }
}
