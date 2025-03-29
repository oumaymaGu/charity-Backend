package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Event;
import tn.example.charity.Entity.User;
import tn.example.charity.Service.IEventService;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/event")

public class EventRestController {
    @Autowired
    private IEventService eventService;


    @PostMapping("/add-Event")
    public Event addEvent(@RequestBody Event e) {
        Event event = eventService.addevent(e);
        return event;
    }

    @DeleteMapping("/remove-event/{event-id}")
    public void removeEvent(@PathVariable("event-id") Long idEvent) {
        eventService.deleteEvent(idEvent);
    }


    @PutMapping("/modifyEvent")
    public Event modifyEvent(@RequestBody Event e) {
        Event event = eventService.modifyEvent(e);
        return event;
    }

    @GetMapping("/retrieve-all-Events")

    public List<Event> getEvents() {
        List<Event> listEvents = eventService.getAllEvents();
        return listEvents;

    }

    @GetMapping("/get-event/{event-id}")
    public Event getevent(@PathVariable("event-id") Long e) {
        Event event = eventService.retrieveEventbyid(e);
        return event;
    }

    @GetMapping("/findByName")
    public List<Event> findByName(@RequestParam("NomEvent") String NomEvent) {
        return eventService.getEventByName(NomEvent);
    }

    @GetMapping("/{eventId}/users")
    public ResponseEntity<List<User>> getUsersByEventId(@PathVariable Long eventId) {
        List<User> users = eventService.getUsersByEventId(eventId);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/nearby")
    public List<Event> getEventsNear(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "50") double radius) {
        return eventService.getEventsNear(latitude, longitude, radius);
    }
}

