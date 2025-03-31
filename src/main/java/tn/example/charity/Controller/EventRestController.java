package tn.example.charity.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.example.charity.Entity.Event;
import tn.example.charity.Entity.User;
import tn.example.charity.Repository.EventRepository;
import tn.example.charity.Service.IEventService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/event")

public class EventRestController {
    @Autowired
    private IEventService eventService;
    @Autowired
    private EventRepository eventRepository;

    @PostMapping("/add-Event")
    public ResponseEntity<Event> addEvent(
            @RequestParam("file") MultipartFile file,
            @RequestParam("event") String eventJson) throws IOException {

        // Convert JSON string to Event object
        ObjectMapper objectMapper = new ObjectMapper();
        Event event = objectMapper.readValue(eventJson, Event.class);

        // Save the image file
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get("uploads/" + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        // Assign image URL to the event
        event.setPhotoEvent("http://localhost:8089/uploads/" + fileName);

        // Save the event using the service
        Event savedEvent = eventService.addevent(event);

        // Return the saved event with ResponseEntity
        return ResponseEntity.ok(savedEvent);
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

