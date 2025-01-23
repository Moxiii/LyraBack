package com.moxi.lyra.Calendar.Event.Handler;

import com.moxi.lyra.Calendar.Calendar;
import com.moxi.lyra.Calendar.CalendarService;
import com.moxi.lyra.Calendar.Event.Event;
import com.moxi.lyra.Calendar.Event.EventService;
import com.moxi.lyra.Calendar.Event.Recurrence.RecurrenceFactory;
import com.moxi.lyra.Calendar.Event.Recurrence.RecurrenceRule;
import com.moxi.lyra.Calendar.Event.Tags.Tags;
import com.moxi.lyra.Calendar.Event.Tags.TagsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Component
public class EventHandler {
@Autowired
private  EventService eventService;
@Autowired
private  CalendarService calendarService;


public List<Event> processEvent(List<Event> event, Calendar calendar) {
	List<Event> processedEvents = new ArrayList<>();
	for (Event e : event) {
		e.setCalendar(calendar);
		handleDefaultDates(e);
		handleEventTags(e, calendar);
		processedEvents.addAll(handleEventRecurrence(e));
	}
	return processedEvents;
}
public List<Event> processUpdateEvent(Event exisistingEvent, Event updatedEvent ,Calendar calendar ) {

	List<Event> processedEvents = new ArrayList<>();

	copyNonNullPropreties(exisistingEvent, updatedEvent);
	handleEventTagsUpdate(exisistingEvent, updatedEvent);
	handleEventCompletionUpdate(exisistingEvent, updatedEvent);
	processedEvents = handleEventRecurrenceUpdate(exisistingEvent, updatedEvent , calendar);

	return processedEvents;
}
public void updateCalendarAndSaveEvents(Calendar calendar, List<Event> eventList) {
	calendarService.saveCalendar(calendar);
	calendar.getEvents().addAll(eventList);
	eventService.saveAllEvents(eventList);

}
private String determinateTag(Event event, Calendar calendar) {
	HashSet set = new HashSet();
	if (Boolean.TRUE.equals(event.isForcePersonalTag())) {
		set.add(Tags.PERSONAL.toString());
		event.setTags(set);
	}
	if (event.getTags() != null && !event.getTags().isEmpty()) {
		String existingTag = event.getTags().iterator().next();
		if (existingTag.startsWith(Tags.CUSTOM.name())) {
			return existingTag;
		}
	}
	if (calendar.getWorkStartTime() == null || calendar.getWorkEndTime() == null) {
		set.add((Tags.PERSONAL.toString()));
		event.setTags(set);
	}
	List<DayOfWeek> workDays = calendar.getWorkDays();
	boolean hashworkDays = workDays != null && !workDays.isEmpty();
	boolean isOnWorkDays = hashworkDays && workDays.contains(event.getStartDate().getDayOfWeek());
	boolean isOnWorkHours = isEventOnWorkHours(event, calendar);
	return (isOnWorkDays && isOnWorkHours) ? Tags.WORK.toString() : Tags.PERSONAL.toString();
}

private boolean isEventOnWorkHours(Event event, Calendar calendar) {
	if (event.getStartHours() == null ||
			calendar.getWorkStartTime() == null ||
			calendar.getWorkEndTime() == null) {
		return false;
	}
	LocalTime startHours = event.getStartHours();
	LocalTime workStartTime = calendar.getWorkStartTime();
	LocalTime workEndTime = calendar.getWorkEndTime();
	return !startHours.isBefore(workStartTime) || !startHours.isAfter(workEndTime);
}

private List<Event> generateRecurringEvents(Event event, Integer interval, RecurrenceRule rule, String unit, LocalDate recurrenceEffectiveEndDate) {
	if (rule == null) {
		throw new IllegalArgumentException("Recurrence rule must be provided");
	}

	if (rule == RecurrenceRule.Custom) {
		if (interval == null || interval <= 0) {
			throw new IllegalArgumentException("Recurrence recurrence interval must be greater than zero for Custom rule");
		}

		if (unit == null || unit.isEmpty() || unit.isBlank()) {
			throw new IllegalArgumentException("Recurrence recurrence unit must be provided for Custom rule");
		}
		event.setRecurrenceRule(rule);
		event.setRecurrenceUnit(unit);
		event.setRecurrenceInterval(interval);

	} else {
		if (rule == RecurrenceRule.Month || rule == RecurrenceRule.Day || rule == RecurrenceRule.Week || rule == RecurrenceRule.Year && interval != null) {
			interval = null;
		}
		if (interval != null || unit != null) {
			throw new IllegalArgumentException("Interval or unit should not be provided for non-Custom recurrence rules");
		}
	}

	final RecurrenceFactory factory = new RecurrenceFactory();
	event.setRecurrenceRule(rule);
	if (rule != RecurrenceRule.Custom) {
		event.setRecurrenceUnit(null);
		event.setRecurrenceInterval(null);
	}

	return factory.generateRecurringEvents(event, recurrenceEffectiveEndDate);
}

private void handleDefaultDates(Event event) {
	if (event.getEndDate() == null) {
		event.setEndDate(event.getStartDate());
	}
	if (event.getStartHours() == null) {
		event.setStartHours(LocalTime.MIN);
	}
	if (event.getEndHours() == null) {
		event.setEndHours(LocalTime.MAX);
	}
}

private void handleEventTags(Event event, Calendar calendar) {
	String determinedTag = determinateTag(event, calendar);

	if(event.getTags() == null && event.getTags().isEmpty()) {
		event.setTags(Collections.singleton(determinedTag));
	}
	if (event.getTags() != null && !event.getTags().isEmpty()) {
		String currentTag = event.getTags().iterator().next();
		if (TagsUtils.isCustomTag(currentTag)) {
			String customTag = TagsUtils.getCustomTag(currentTag);
			event.setTags(Collections.singleton(Tags.custom(customTag)));
		}
	}
}

private List<Event> handleEventRecurrence(Event event) {
	List<Event> eventList = new ArrayList<>();
	if (event.getRecurrenceRule() != null) {
		LocalDate recurrenceEffectiveEndDate = calculateRecurrenceEndDate(event);
		List<Event> recurringEvents = generateRecurringEvents(event, event.getRecurrenceInterval(),
				event.getRecurrenceRule(), event.getRecurrenceUnit(), recurrenceEffectiveEndDate);
		eventList.addAll(recurringEvents);
	} else {
		eventList.add(event);
	}
	return eventList;
}

private LocalDate calculateRecurrenceEndDate(Event event) {
	LocalDate recurrenceEffectiveEndDate = event.getRecurrenceEndDate();

	if (recurrenceEffectiveEndDate == null && event.getRecurrenceDuration() != null) {
		recurrenceEffectiveEndDate = event.getStartDate().plus(event.getRecurrenceDuration());
	}

	if (recurrenceEffectiveEndDate == null) {
		recurrenceEffectiveEndDate = event.getStartDate().plusMonths(6);
	}

	return recurrenceEffectiveEndDate;
}

private void handleEventTagsUpdate(Event existingEvent, Event updatedEvents) {
	if (updatedEvents.getTags() != null && !updatedEvents.getTags().equals(existingEvent.getTags())) {
		existingEvent.setTags(updatedEvents.getTags());
	}
}

private void handleEventCompletionUpdate(Event existingEvent, Event updatedEvents) {
	if (updatedEvents.isCompleted()) {
		existingEvent.setCompleted(true);
	}
}

private List<Event> handleEventRecurrenceUpdate(Event existingEvent, Event updatedEvents , Calendar calendar) {
	List<Event> processedEvents = new ArrayList<>();
	if (updatedEvents.getRecurrenceRule() != null) {
		processedEvents = handleEventRecurrence(updatedEvents);
		eventService.deleteRecurringEvents(existingEvent);
	} else {
		processedEvents.add(existingEvent);
	}
	return processedEvents;
}

private void copyNonNullPropreties(Event existingEvent , Event updatedEvent){
	BeanUtils.copyProperties(updatedEvent, existingEvent , getNullPropertyNames(updatedEvent));
}
private String[] getNullPropertyNames(Object source) {
	final BeanWrapper src = new BeanWrapperImpl(source);
	return Arrays.stream(src.getPropertyDescriptors())
			.filter(pd-> src.getPropertyValue(pd.getName()) == null)
			.toArray(String[]::new);
}
}
