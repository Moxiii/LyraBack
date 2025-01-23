package com.moxi.lyra.Calendar;


import com.moxi.lyra.Calendar.Event.EventService;
import com.moxi.lyra.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {
    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private EventService eventService;
    public Calendar findByUser(User user) {
        return calendarRepository.findByUser(user);
    }
    public void deleteCalendarByUser(User user) {
        Calendar calendar =  calendarRepository.findByUser(user);
        if (calendar != null) {
            if(calendar.getEvents() != null){
				eventService.deleteAll(calendar.getEvents());
                calendar.getEvents().clear();
            }
            calendarRepository.delete(calendar);
        }
    }
    public void saveCalendar(Calendar calendar) {
        calendarRepository.save(calendar);
    }
}
