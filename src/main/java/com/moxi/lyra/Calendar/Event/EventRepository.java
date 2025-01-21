package com.moxi.lyra.Calendar.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStartDate (LocalDate startDate);
    List<Event> findByStartDateBetween (LocalDate startDate , LocalDate endDate);
}

