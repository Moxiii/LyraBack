package org.georges.georges.DTO;

import lombok.Getter;
import lombok.Setter;
import org.georges.georges.Calendar.Event.Event;

import java.util.List;

@Getter
@Setter
public class CalendarRes {
    private Long id;
    private String username;
    private List<Event> eventsList;
}
