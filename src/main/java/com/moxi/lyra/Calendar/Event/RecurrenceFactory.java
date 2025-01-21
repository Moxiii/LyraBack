package com.moxi.lyra.Calendar.Event;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RecurrenceFactory {
public List<Event> generateRecurringEvents(Event event ,LocalDate recurrenceEffectiveEndDate) {
    List<Event> recurringEvents = new ArrayList<>();
    LocalDate currentDate = event.getStartDate();


    if (recurrenceEffectiveEndDate == null && event.getRecurrenceDuration() != null) {
        recurrenceEffectiveEndDate = event.getStartDate().plus(event.getRecurrenceDuration());
    }
    if (recurrenceEffectiveEndDate == null) {
        recurrenceEffectiveEndDate = event.getStartDate().plusMonths(6);
    }

    if (event.getRecurrenceRule() == null) {
        throw new IllegalArgumentException("Recurrence rule must be provided.");
    }

    while (!currentDate.isAfter(recurrenceEffectiveEndDate)) {
        Event recurringEvent = createRecurringEvent(event, currentDate);
        recurringEvents.add(recurringEvent);

        currentDate = switch (event.getRecurrenceRule()) {
            case Day -> currentDate.plusDays(1);
            case Week -> currentDate.plusWeeks(1);
            case Month -> currentDate.plusMonths(1);
            case Year -> currentDate.plusYears(1);
            case Custom -> {
                if (event.getRecurrenceInterval() == null || event.getRecurrenceInterval() <= 0) {
                    throw new IllegalArgumentException("Custom recurrence interval must be a positive integer.");
                }
                if (event.getRecurrenceUnit() == null || event.getRecurrenceUnit().isBlank()) {
                    throw new IllegalArgumentException("Custom recurrence unit must be provided and valid.");
                }

                yield switch (event.getRecurrenceUnit()) {
                    case "Day" -> currentDate.plusDays(event.getRecurrenceInterval());
                    case "Week" -> currentDate.plusWeeks(event.getRecurrenceInterval());
                    case "Month" -> currentDate.plusMonths(event.getRecurrenceInterval());
                    case "Year" -> currentDate.plusYears(event.getRecurrenceInterval());
                    default -> throw new RuntimeException("Unsupported recurrence unit: " + event.getRecurrenceUnit());
                };
            }
        };
    }

    return recurringEvents;
}


private Event createRecurringEvent(Event originalEvent, LocalDate startDate) {
        Event recurringEvent = new Event();
        BeanUtils.copyProperties(originalEvent , recurringEvent );
        recurringEvent.setId(null);
        recurringEvent.setStartDate(startDate);
        long durationDays = originalEvent.getEndDate() != null
            ? originalEvent.getEndDate().toEpochDay() - originalEvent.getStartDate().toEpochDay()
            : 0;
        if (durationDays > 0) {
            recurringEvent.setEndDate(startDate.plusDays(durationDays));
        }else{
            recurringEvent.setEndDate(startDate);
        }
        return recurringEvent;
    }
}
