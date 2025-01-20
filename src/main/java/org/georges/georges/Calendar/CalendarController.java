package org.georges.georges.Calendar;


import org.georges.georges.Calendar.event.Event;
import org.georges.georges.Calendar.event.EventRepository;
import org.georges.georges.Config.CustomAnnotation.RequireAuthorization;
import org.georges.georges.Config.Utils.JwtUtil;
import org.georges.georges.Config.Utils.SecurityUtils;
import org.georges.georges.DTO.CalendarRes;
import org.georges.georges.User.User;
import org.georges.georges.User.UserRepository;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@RequireAuthorization
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
   private CalendarRepository calendarRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CalendarService calendarService;

   private String[] getNullPropertyNames(Object source) {
       final BeanWrapper src = new BeanWrapperImpl(source);
       return Arrays.stream(src.getPropertyDescriptors())
               .filter(pd-> src.getPropertyValue(pd.getName()) == null)
               .toArray(String[]::new);
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
            return ResponseEntity.ok(calendarRes);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCalendar( ) {
            User currentUser = SecurityUtils.getCurrentUser();
            Calendar calendar = calendarRepository.findByUser(currentUser);
            calendarRepository.delete(calendar);
            return ResponseEntity.ok(Map.of("message", "Calendar deleted"));
    }
    //Gestion des tache /evenement journalier
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
                    event.setStartHours(LocalTime.parse("00:00"));
                }
                if (event.getEndHours() == null) {
                    event.setEndHours(LocalTime.parse("23:59"));
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
