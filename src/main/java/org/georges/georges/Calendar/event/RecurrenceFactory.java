package org.georges.georges.Calendar.event;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RecurrenceFactory {
    public List<Event> generateRecurringEvents(Event event) {

        List<Event> recurringEvents = new ArrayList<>();

        if(event.getRecurrenceRule() == RecurrenceRule.Custom){
            LocalDate currentDate = event.getStartDate();
            LocalDate endDate = event.getEndDate();
            if (event.getRecurrenceInterval() == null || event.getRecurrenceInterval() <= 0) {
                throw new IllegalArgumentException("Recurrence interval must be a positive integer");
            }
            if (event.getRecurrenceUnit() == null || event.getRecurrenceUnit().isBlank()) {
                throw new IllegalArgumentException("Recurrence unit must be provided and valid");
            }
            long durationsDays = event.getEndDate().toEpochDay() - currentDate.toEpochDay();
            while(currentDate.isBefore(endDate) || currentDate.isEqual(endDate) ){
                Event recurringEvent = new Event();
                BeanUtils.copyProperties(event, recurringEvent);
                recurringEvent.setId(null);
                recurringEvent.setStartDate(currentDate);
                recurringEvent.setEndDate(LocalDate.ofEpochDay(durationsDays));
                recurringEvents.add(recurringEvent);

                switch(event.getRecurrenceUnit()){
                    case "Day" -> currentDate = currentDate.plusDays(event.getRecurrenceInterval());
                    case "Week" -> currentDate = currentDate.plusWeeks(event.getRecurrenceInterval());
                    case "Month" -> currentDate = currentDate.plusMonths(event.getRecurrenceInterval());
                    case "Year" -> currentDate = currentDate.plusYears(event.getRecurrenceInterval());
                    default -> throw new RuntimeException("Unsupported recurrence unit " + event.getRecurrenceUnit());

                }
            }
        }
        return recurringEvents;
    }

}
