package tn.example.charity.Service;

import tn.example.charity.Entity.Event;
import tn.example.charity.Entity.User;


import java.util.List;

public interface IEventService {
    Event addevent(Event event);
    void deleteEvent(Long idEvent);
    Event modifyEvent(Event event);
    List<Event> getAllEvents();
    Event retrieveEventbyid(Long idEevent);
    List<Event> getEventByName(String eventName);
    List<User>getUsersByEventId(Long eventId);
    List<Event> getEventsNear(Double latitude, Double longitude, double radius);
}

