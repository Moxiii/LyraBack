package com.moxi.lyra.Calendar;


import lombok.extern.slf4j.Slf4j;
import com.moxi.lyra.Calendar.Event.*;
import com.moxi.lyra.Config.CustomAnnotation.RequireAuthorization;
import com.moxi.lyra.Config.Utils.SecurityUtils;
import com.moxi.lyra.DTO.CalendarRes;
import com.moxi.lyra.User.User;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
@Slf4j
@RequireAuthorization
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private EventService eventService;

private String[] getNullPropertyNames(Object source) {
       final BeanWrapper src = new BeanWrapperImpl(source);
       return Arrays.stream(src.getPropertyDescriptors())
               .filter(pd-> src.getPropertyValue(pd.getName()) == null)
               .toArray(String[]::new);
   }
private List<Event> generateRecurringEvents(Event event , Integer interval , RecurrenceRule rule , String unit , LocalDate recurrenceEffectiveEndDate){
    if (rule == null) {
        throw new IllegalArgumentException("Recurrence rule must be provided");
    }

    if(rule == RecurrenceRule.Custom){
        if(interval == null || interval <= 0 ){
            throw new IllegalArgumentException("Recurrence recurrence interval must be greater than zero for Custom rule");
        }

        if(unit == null || unit.isEmpty() || unit.isBlank()){
            throw new IllegalArgumentException("Recurrence recurrence unit must be provided for Custom rule");
        }
        event.setRecurrenceRule(rule);
        event.setRecurrenceUnit(unit);
        event.setRecurrenceInterval(interval);

    } else {
        if(rule == RecurrenceRule.Month || rule == RecurrenceRule.Day || rule == RecurrenceRule.Week || rule == RecurrenceRule.Year && interval != null){
            log.warn("Interval should not be provided for this rule recurrence, resetting interval.");
            interval = null;
        }
        if(interval != null || unit != null){
            throw new IllegalArgumentException("Interval or unit should not be provided for non-Custom recurrence rules");
        }
    }

    final RecurrenceFactory factory = new RecurrenceFactory();
    event.setRecurrenceRule(rule);
    if(rule != RecurrenceRule.Custom) {
        event.setRecurrenceUnit(null);
        event.setRecurrenceInterval(null);
    }

    return factory.generateRecurringEvents(event , recurrenceEffectiveEndDate);
}
    @GetMapping("/get")
    public ResponseEntity<?> getCalendar() {
            User currentUser = SecurityUtils.getCurrentUser();
            Calendar calendar = calendarService.findByUser(currentUser);
            CalendarRes calendarRes = new CalendarRes();
            calendarRes.setId(calendar.getId());
            calendarRes.setUsername(currentUser.getUsername());
            calendarRes.setEventsList(calendar.getEvents());
            return ResponseEntity.ok(calendarRes);
    }

    @GetMapping("/getByWeek")
    public ResponseEntity<?> getCalendarByWeek(@RequestBody LocalDate week) {
       LocalDate firstDay = week.with(DayOfWeek.MONDAY);
       LocalDate lastDay = firstDay.plusDays(6);
       List<Event> events = eventService.findByStartDateBetween(firstDay, lastDay);
       return ResponseEntity.ok(events);
    }
    @GetMapping("/getByMonth")
    public ResponseEntity<?> getCalendarByMonth(@RequestBody LocalDate month) {
       LocalDate selectedMonth = month.withMonth(month.getMonthValue());
       LocalDate firstDay = selectedMonth.withDayOfMonth(1);
       LocalDate lastDay = selectedMonth.with(TemporalAdjusters.lastDayOfMonth());
       List<Event> events = eventService.findByStartDateBetween(firstDay, lastDay);
       return ResponseEntity.ok(events);
    }

    //Gestion des tâches /événement journalier
    @GetMapping("/event/get")
    public ResponseEntity<?> getDailyEvent(  @RequestBody LocalDate day) {
            List<Event> dayEvent = eventService.findByStartDate(day);
            return ResponseEntity.ok(dayEvent);
    }
    @GetMapping("/event/{eventID}")
    public ResponseEntity<?> getEventByID(  @PathVariable long eventID) {
            Event existingEvent = eventService.findById(eventID);
            return ResponseEntity.ok(existingEvent);
    }
@PostMapping("/event/add")
public ResponseEntity<?> addEvent(@RequestBody List<Event> events) {
    User currentUser = SecurityUtils.getCurrentUser();
    List<Event> newEvents = new ArrayList<>();
    Calendar calendar = calendarService.findByUser(currentUser);
    for (Event event : events) {
        if (event.getEndDate() == null) {
            event.setEndDate(event.getStartDate());
        }
        if (event.getStartHours() == null) {
            event.setStartHours(LocalTime.MIN);
        }
        if (event.getEndHours() == null) {
            event.setEndHours(LocalTime.MAX);
        }
        if (event.getRecurrenceRule() != null) {
            if (event.getRecurrenceEndDate() != null && event.getRecurrenceDuration() != null) {
                throw new IllegalArgumentException("Cannot provide both recurrenceEndDate and recurrenceDuration.");
            }
            LocalDate recurrenceEffectiveEndDate = event.getRecurrenceEndDate();
            if (recurrenceEffectiveEndDate == null && event.getRecurrenceDuration() != null) {
                recurrenceEffectiveEndDate = event.getStartDate().plus(event.getRecurrenceDuration());
            }
            if (recurrenceEffectiveEndDate == null) {
                recurrenceEffectiveEndDate = event.getStartDate().plusMonths(6);
            }
            List<Event> recurringEvents = generateRecurringEvents(event, event.getRecurrenceInterval(),
                    event.getRecurrenceRule(), event.getRecurrenceUnit(), recurrenceEffectiveEndDate);
            newEvents.addAll(recurringEvents);
        } else {
            newEvents.add(event);
        }
    }
    calendar.getEvents().addAll(newEvents);
    eventService.saveAllEvents(newEvents);
    calendarService.saveCalendar(calendar);

    return ResponseEntity.ok(newEvents);
}


    @PutMapping("/event/update/{eventID}")
    public ResponseEntity<?> updateEvent(  @PathVariable long eventID , @RequestBody Event updatedEvents) {
            User currentUser = SecurityUtils.getCurrentUser();
            Event existingEvent = eventService.findById(eventID);
            Calendar calendar = calendarService.findByUser(currentUser);
            BeanUtils.copyProperties(updatedEvents, existingEvent, getNullPropertyNames(updatedEvents));
        if (updatedEvents.getTags() != null && !updatedEvents.getTags().equals(existingEvent.getTags())) {
            existingEvent.setTags(updatedEvents.getTags());
        }
        if (updatedEvents.isCompleted()) {
            existingEvent.setCompleted(true);
        }
        if(updatedEvents.getRecurrenceRule() != null) {
            List<Event> recurringEvents = generateRecurringEvents(updatedEvents,updatedEvents.getRecurrenceInterval(),updatedEvents.getRecurrenceRule(),updatedEvents.getRecurrenceUnit() , updatedEvents.getRecurrenceEndDate());
            calendar.setEvents(recurringEvents);
        }
            calendarService.saveCalendar(calendar);
            existingEvent.setCalendar(calendar);
            eventService.saveEvent(existingEvent);
            return ResponseEntity.ok(existingEvent);
    }
    @DeleteMapping("/event/delete/{eventID}")
    public ResponseEntity<?> deleteEvent( @PathVariable long eventID) {
            Event existingEvent = eventService.findById(eventID);
            Calendar calendar = existingEvent.getCalendar();
            calendar.getEvents().remove(existingEvent);
            eventService.deleteEvent(existingEvent);
            return ResponseEntity.ok(Map.of("message", "Event deleted"));
        }
}
