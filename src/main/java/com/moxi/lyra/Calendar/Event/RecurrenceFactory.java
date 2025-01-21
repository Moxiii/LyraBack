package com.moxi.lyra.Calendar.Event;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RecurrenceFactory {
    public List<Event> generateRecurringEvents(Event event) {
        List<Event> recurringEvents = new ArrayList<>();
        LocalDate currentDate = event.getStartDate();
        LocalDate endDate = event.getEndDate();
        long durationDays = event.getEndDate().toEpochDay() - event.getStartDate().toEpochDay();

        if (event.getRecurrenceRule() == null) {
            throw new IllegalArgumentException("Recurrence rule must be provided.");
        }

        switch (event.getRecurrenceRule()) {
            case RecurrenceRule.Day -> {
                while (!currentDate.isAfter(endDate)) {
                    recurringEvents.add(createRecurringEvent(event, currentDate, durationDays));
                    currentDate = currentDate.plusDays(1);
                }
            }
            case RecurrenceRule.Week -> {
                while (!currentDate.isAfter(endDate)) {
                    recurringEvents.add(createRecurringEvent(event, currentDate, durationDays));
                    currentDate = currentDate.plusWeeks(1);
                }
            }
            case RecurrenceRule.Month -> {
                while (!currentDate.isAfter(endDate)) {
                    recurringEvents.add(createRecurringEvent(event, currentDate, durationDays));
                    currentDate = currentDate.plusMonths(1);
                }
            }
            case RecurrenceRule.Year -> {
                while (!currentDate.isAfter(endDate)) {
                    recurringEvents.add(createRecurringEvent(event, currentDate, durationDays));
                    currentDate = currentDate.plusYears(1);
                }
            }
            case Custom -> {
                if (event.getRecurrenceInterval() == null || event.getRecurrenceInterval() <= 0) {
                    throw new IllegalArgumentException("Custom recurrence interval must be a positive integer.");
                }
                if (event.getRecurrenceUnit() == null || event.getRecurrenceUnit().isBlank()) {
                    throw new IllegalArgumentException("Custom recurrence unit must be provided and valid.");
                }

                while (!currentDate.isAfter(endDate)) {
                    recurringEvents.add(createRecurringEvent(event, currentDate, durationDays));
                    currentDate = switch (event.getRecurrenceUnit()) {
                        case "Day" -> currentDate.plusDays(event.getRecurrenceInterval());
                        case "Week" -> currentDate.plusWeeks(event.getRecurrenceInterval());
                        case "Month" -> currentDate.plusMonths(event.getRecurrenceInterval());
                        case "Year" -> currentDate.plusYears(event.getRecurrenceInterval());
                        default -> throw new RuntimeException("Unsupported recurrence unit: " + event.getRecurrenceUnit());
                    };
                }
            }
            default -> throw new RuntimeException("Unsupported recurrence rule: " + event.getRecurrenceRule());
        }

        return recurringEvents;
    }
    private Event createRecurringEvent(Event originalEvent, LocalDate startDate, long durationDays) {
        Event recurringEvent = new Event();
        BeanUtils.copyProperties(originalEvent , recurringEvent );
        recurringEvent.setId(null);
        recurringEvent.setStartDate(startDate);
        recurringEvent.setEndDate(startDate.plusDays(durationDays));
        return recurringEvent;
    }
}
