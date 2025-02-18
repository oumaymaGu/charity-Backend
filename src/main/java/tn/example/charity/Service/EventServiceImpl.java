package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Event;
import tn.example.charity.Repository.EventRepository;

import java.util.List;
@Service
@AllArgsConstructor
@Slf4j

public class EventServiceImpl implements IEventService {
    @Autowired
      private EventRepository eventRepository;
    @Override
    public Event addevent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long idEvent) {
        eventRepository.deleteById(idEvent);

    }

    @Override
    public Event modifyEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event retrieveEventbyid(Long idEevent) {
        return eventRepository.findById(idEevent).get();
    }
}


