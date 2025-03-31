package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Event;
import tn.example.charity.Entity.User;
import tn.example.charity.Repository.EventRepository;
import tn.example.charity.dto.WeatherInfo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j

public class EventServiceImpl implements IEventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private WeatherService weatherService;


    @Override
    public Event addevent(Event event) {
        // 1. Récupérer les coordonnées de la ville
        double[] coords = geocodingService.getCoordinates(event.getLieu());
        event.setLatitude(coords[0]);
        event.setLongitude(coords[1]);

        // 2. Convertir Date en LocalDate
        LocalDate localDate = event.getDateEvent().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // 3. Récupérer la météo pour la date et le lieu
        WeatherInfo weatherInfo = weatherService.getWeather(coords[0], coords[1], localDate);

        // 4. Ajouter les infos météo à l'événement
        event.setTemperature(weatherInfo.getTemperature());
        event.setWeatherDescription(weatherInfo.getDescription());

        // 5. Sauvegarder l'événement
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

    @Override
    public List<Event> getEventByName(String eventName) {
        return eventRepository.findEventByNomEvent(eventName);
    }


    public List<User> getUsersByEventId(Long eventId) {
        return eventRepository.findUsersByEventId(eventId);
    }

    @Override
    public List<Event> getEventsNear(Double latitude, Double longitude, double radius) {
        return   eventRepository.findEventsNear(latitude, longitude, radius);
    }


}


