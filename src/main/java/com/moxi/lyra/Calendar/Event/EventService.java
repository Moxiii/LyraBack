package com.moxi.lyra.Calendar.Event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.lyra.Calendar.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final  String REDIS_EVENT_QUEUE = "event_queue";
    private static final int MY_SQL_EVENT_LIMIT = 100;
@Qualifier("objectMapper")
@Autowired
private ObjectMapper objectMapper;

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
        long existingEvents = events.size();
        if(existingEvents > MY_SQL_EVENT_LIMIT) {
            List<Event> eventToMysql = events.subList(0, MY_SQL_EVENT_LIMIT);
            List<Event> eventToRedis = events.subList(MY_SQL_EVENT_LIMIT, events.size());
            if(!eventToMysql.isEmpty()) {
                eventRepository.saveAll(eventToMysql);
            }
            if(!eventToRedis.isEmpty()) {
                ListOperations<String , Object> eventListOps = redisTemplate.opsForList();
                for(Event event : eventToRedis) {
                    eventListOps.leftPush(REDIS_EVENT_QUEUE, event);
                }
            }
        }
        eventRepository.saveAll(events);
    }
    public void transfertEventsFromRedisToMysql(){
        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        Object rawEvent;
        while((rawEvent = listOps.leftPop(REDIS_EVENT_QUEUE)) != null) {
            Event event;
            try{
                event = objectMapper.convertValue(rawEvent, Event.class);
            }catch(Exception e){
                throw new RuntimeException("non valid Event Object");
            }
            saveEvent(event);
        }
    }
public void deleteAll(List<Event> events) {
        eventRepository.deleteAll(events);
}

public void deleteRecurringEvents(Event existingEvent) {
        eventRepository.delete(existingEvent);
}
}
