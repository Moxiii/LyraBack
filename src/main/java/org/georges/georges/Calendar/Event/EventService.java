package org.georges.georges.Calendar.Event;

import org.georges.georges.Calendar.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    public List<Event> findByStartDate(LocalDate startDate) {
       return eventRepository.findByStartDate(startDate);
    }
    public List<Event> findByStartDateBetween(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findByStartDateBetween(startDate, endDate);
    }
    public Event findById(Long id) {
        if(eventRepository.existsById(id)) {
            return eventRepository.findById(id).get();
        }
        return null;
    }
    public void saveEvent(Event event) {
         eventRepository.save(event);
    }
    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }
    public void saveAllEvents(List<Event> events) {
        eventRepository.saveAll(events);
    }
public void deleteAll(List<Event> events) {
        eventRepository.deleteAll(events);
}
}
