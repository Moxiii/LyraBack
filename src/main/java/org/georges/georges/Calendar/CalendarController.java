package org.georges.georges.Calendar;


import org.georges.georges.Calendar.event.Event;
import org.georges.georges.Calendar.event.EventRepository;
import org.georges.georges.Calendar.event.RecurrenceFactory;
import org.georges.georges.Calendar.event.RecurrenceRule;
import org.georges.georges.Config.CustomAnnotation.RequireAuthorization;
import org.georges.georges.Config.Utils.SecurityUtils;
import org.georges.georges.DTO.CalendarRes;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;
import java.time.*;
import java.util.*;

@RequireAuthorization
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    @Autowired
   private CalendarRepository calendarRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;

   private String[] getNullPropertyNames(Object source) {
       final BeanWrapper src = new BeanWrapperImpl(source);
       return Arrays.stream(src.getPropertyDescriptors())
               .filter(pd-> src.getPropertyValue(pd.getName()) == null)
               .toArray(String[]::new);
   }
   private List<Event> generateRecurringEvents(Event event , int interval , RecurrenceRule rule , String unit){
       if (interval <= 0) {
           throw new IllegalArgumentException("Interval must be greater than 0.");
       }
       if (!List.of("Day", "Week", "Month", "Year").contains(unit)) {
           throw new IllegalArgumentException("Invalid recurrence unit: " + unit);
       }
       final RecurrenceFactory factory = new RecurrenceFactory();
       event.setRecurrenceRule(rule);
       event.setRecurrenceUnit(unit);
       event.setRecurrenceInterval(interval);
       return factory.generateRecurringEvents(event);
   }
    @GetMapping("/get")
    public ResponseEntity<?> getCalendar() {
            User currentUser = SecurityUtils.getCurrentUser();
            Calendar calendar = calendarRepository.findByUser(currentUser);
            CalendarRes calendarRes = new CalendarRes();
            calendarRes.setId(calendar.getId());
            calendarRes.setUsername(currentUser.getUsername());
            calendarRes.setEventsList(calendar.getEvents());
            return ResponseEntity.ok(calendarRes);
    }

    @GetMapping("/getByWeek")
    public ResponseEntity<?> getCalendarByWeek(@RequestBody LocalDate week) {
       LocalDate firstDay = week.with(DayOfWeek.MONDAY);
       LocalDate lastDay = week.with(DayOfWeek.SUNDAY);
       List<Event> events = eventRepository.findByStartDateBetween(firstDay, lastDay);
       CalendarRes calendarRes = new CalendarRes();
       calendarRes.setEventsList(events);
       return ResponseEntity.ok(calendarRes);
    }
    @GetMapping("/getByMonth")
    public ResponseEntity<?> getCalendarByMonth(@RequestBody LocalDate month) {
       LocalDate selectedMonth = month.withMonth(month.getMonthValue());
       LocalDate firstDay = selectedMonth.withDayOfMonth(1);
       LocalDate lastDay = selectedMonth.withDayOfMonth(selectedMonth.getDayOfMonth());
       List<Event> events = eventRepository.findByStartDateBetween(firstDay, lastDay);
       CalendarRes calendarRes = new CalendarRes();
       calendarRes.setEventsList(events);
       return ResponseEntity.ok(calendarRes);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCalendar( ) {
            User currentUser = SecurityUtils.getCurrentUser();
            Calendar calendar = new Calendar();
            currentUser.setCalendar(calendar);
            calendar.setUser(currentUser);
            CalendarRes calendarRes = new CalendarRes();
            calendarRes.setUsername(currentUser.getUsername());
            calendarRes.setId(calendar.getId());
            userRepository.save(currentUser);
            calendarRepository.save(calendar);
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarRes);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCalendar( ) {
            User currentUser = SecurityUtils.getCurrentUser();
            calendarRepository.delete(calendarRepository.findByUser(currentUser));
            return ResponseEntity.ok(Map.of("message", "Calendar deleted"));
    }
    //Gestion des tâches /événement journalier
    @GetMapping("/event/get")
    public ResponseEntity<?> getDailyEvent(  @RequestBody LocalDate day) {
            List<Event> dayEvent = eventRepository.findByStartDate(day);
            return ResponseEntity.ok(dayEvent);
    }
    @GetMapping("/event/{eventID}")
    public ResponseEntity<?> getEventByID(  @PathVariable long eventID) {
            Event existingEvent = eventRepository.findById(eventID).orElseThrow(()-> new RuntimeException("Event with id " + eventID + " not found"));
            return ResponseEntity.ok(existingEvent);
    }
    @PostMapping("/event/add")
    public ResponseEntity<?> addEvent(  @RequestBody List<Event> events) {
            User currentUser = SecurityUtils.getCurrentUser();
            List<Event> newEvents = new ArrayList<>();
            Calendar calendar = calendarRepository.findByUser(currentUser);
            for (Event event : events) {
                event.setCalendar(calendar);
                if (event.getStartHours() == null) {
                    event.setStartHours(LocalTime.MIN);
                }
                if (event.getEndHours() == null) {
                    event.setEndHours(LocalTime.MAX);
                }
                if(event.getRecurrenceRule() != null) {
                    List<Event> recurringEvents = generateRecurringEvents(event , event.getRecurrenceInterval() , event.getRecurrenceRule(),event.getRecurrenceUnit());
                    newEvents.addAll(recurringEvents);
                }
                newEvents.add(event);
                calendar.getEvents().add(event);
            }
            eventRepository.saveAll(newEvents);
            calendarRepository.save(calendar);
            return ResponseEntity.ok(newEvents);
    }

    @PutMapping("/event/update/{eventID}")
    public ResponseEntity<?> updateEvent(  @PathVariable long eventID , @RequestBody Event updatedEvents) {
            User currentUser = SecurityUtils.getCurrentUser();
            Event existingEvent = eventRepository.findById(eventID).orElseThrow(()-> new RuntimeException("Event not found"));
            Calendar calendar = calendarRepository.findByUser(currentUser);
            BeanUtils.copyProperties(updatedEvents, existingEvent, getNullPropertyNames(updatedEvents));
        if (updatedEvents.getTags() != null && !updatedEvents.getTags().equals(existingEvent.getTags())) {
            existingEvent.setTags(updatedEvents.getTags());
        }
        if (updatedEvents.isCompleted()) {
            existingEvent.setCompleted(true);
        }
        if(updatedEvents.getRecurrenceRule() != null) {
            List<Event> recurringEvents = generateRecurringEvents(updatedEvents,updatedEvents.getRecurrenceInterval(),updatedEvents.getRecurrenceRule(),updatedEvents.getRecurrenceUnit());
            calendar.setEvents(recurringEvents);
        }
            calendarRepository.save(calendar);
            existingEvent.setCalendar(calendar);
            eventRepository.save(existingEvent);
            return ResponseEntity.ok(existingEvent);
    }
    @DeleteMapping("/event/delete/{eventID}")
    public ResponseEntity<?> deleteEvent( @PathVariable long eventID) {
            Event existingEvent = eventRepository.findById(eventID).orElseThrow(()-> new RuntimeException("Event not found"));
            Calendar calendar = existingEvent.getCalendar();
            calendar.getEvents().remove(existingEvent);
            eventRepository.delete(existingEvent);
            return ResponseEntity.ok(Map.of("message", "Event deleted"));
        }
}
