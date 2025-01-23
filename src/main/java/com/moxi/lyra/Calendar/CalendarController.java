package com.moxi.lyra.Calendar;


import com.moxi.lyra.Calendar.Event.Handler.EventHandler;
import lombok.extern.slf4j.Slf4j;
import com.moxi.lyra.Calendar.Event.*;
import com.moxi.lyra.Config.CustomAnnotation.RequireAuthorization;
import com.moxi.lyra.Config.Utils.SecurityUtils;
import com.moxi.lyra.DTO.CalendarRes;
import com.moxi.lyra.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    @Autowired
    private EventHandler eventHandler;




@PostMapping("/set/workingDays")
public ResponseEntity<CalendarRes> setWorkingDays(@RequestBody List<DayOfWeek> workingDays ){
    User currentUser = SecurityUtils.getCurrentUser();
    Calendar calendar = calendarService.findByUser(currentUser);
    calendar.setWorkDays(workingDays);
    CalendarRes calendarRes = new CalendarRes();
    calendarRes.setWorkingDay(workingDays);
    return ResponseEntity.ok(calendarRes);
}
@PutMapping("/update/workingDays")
public ResponseEntity<CalendarRes> updateWorkingDays(@RequestBody List<DayOfWeek> updatedWorkingDays ){
    User currentUser = SecurityUtils.getCurrentUser();
    Calendar calendar = calendarService.findByUser(currentUser);
    calendar.setWorkDays(updatedWorkingDays);
    CalendarRes calendarRes = new CalendarRes();
    calendarRes.setWorkingDay(updatedWorkingDays);
    return ResponseEntity.ok(calendarRes);
}

@PostMapping("/set/workingHours")
public ResponseEntity<CalendarRes> setWorkingHours(@RequestBody LocalTime startHour , @RequestBody LocalTime endHour){
    User currentUser = SecurityUtils.getCurrentUser();
    Calendar calendar = calendarService.findByUser(currentUser);
    calendar.setWorkStartTime(startHour);
    calendar.setWorkEndTime(endHour);
    CalendarRes calendarRes = new CalendarRes();
    calendarRes.setId(calendar.getId());
    calendarRes.setWorkingHours(List.of(startHour, endHour));
    return ResponseEntity.ok(calendarRes);
}
@PostMapping("/update/workingHours")
public ResponseEntity<CalendarRes> updateWorkingHours(@RequestBody LocalTime updatedStartHour , @RequestBody LocalTime updatedEndHour){
    User currentUser = SecurityUtils.getCurrentUser();
    Calendar calendar = calendarService.findByUser(currentUser);
    calendar.setWorkStartTime(updatedStartHour);
    calendar.setWorkEndTime(updatedEndHour);
    CalendarRes calendarRes = new CalendarRes();
    calendarRes.setId(calendar.getId());
    calendarRes.setWorkingHours(List.of(updatedEndHour, updatedStartHour));
    return ResponseEntity.ok(calendarRes);
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
    Calendar calendar = calendarService.findByUser(currentUser);
    List<Event> processedEvents = eventHandler.processEvent(events, calendar);
    eventHandler.updateCalendarAndSaveEvents(calendar, processedEvents);
    return ResponseEntity.ok(processedEvents);
}


    @PutMapping("/event/update/{eventID}")
    public ResponseEntity<?> updateEvent(  @PathVariable long eventID , @RequestBody Event updatedEvents) {
            User currentUser = SecurityUtils.getCurrentUser();
            Event existingEvent = eventService.findById(eventID);
            Calendar calendar = calendarService.findByUser(currentUser);
            List<Event> processedEvents = eventHandler.processUpdateEvent(existingEvent, updatedEvents, calendar);
        calendar.getEvents().removeIf(e -> processedEvents.stream().noneMatch(pe -> pe.getId().equals(e.getId())));
        calendar.getEvents().addAll(processedEvents);
        calendarService.saveCalendar(calendar);
        eventService.saveAllEvents(processedEvents);
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
