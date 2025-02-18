package tn.example.charity.Service;

import tn.example.charity.Entity.Event;


import java.util.List;

public interface IEventService {
    Event addevent(Event event);
    void deleteEvent(Long idEvent);
    Event modifyEvent(Event event);
    List<Event> getAllEvents();
    Event retrieveEventbyid(Long idEevent);
}

