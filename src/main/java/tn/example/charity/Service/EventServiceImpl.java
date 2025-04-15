package tn.example.charity.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Event;
import tn.example.charity.Entity.User;
import tn.example.charity.Repository.EventRepository;
import tn.example.charity.Repository.UserRepository;
import tn.example.charity.dto.BilletDTO;
import tn.example.charity.dto.WeatherInfo;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
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
    @Autowired
    private QRCodeService qrCodeService ;
    @Autowired
    private UserRepository userRepository;


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

    @Override
    public List<Event> getEventsWithLogestiques() {
        return eventRepository.findAll();
    }
    @Override
    public BilletDTO generateBillet(Long eventId, Long userId) {
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            String qrText = "Event: " + event.getNomEvent() + "\n"
                    + "Date: " + event.getDateEvent() + "\n"
                    + "Participant: " + user.getUsername();

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 250, 250);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            String qrBase64 = Base64.getEncoder().encodeToString(pngData);

            return new BilletDTO(
                    event.getNomEvent(),
                    event.getDateEvent().toString(),
                    user.getUsername(),
                    qrBase64
            );
        } catch (Exception e) {
            e.printStackTrace(); // 👈 pour voir dans la console backend ce qui se passe
            throw new RuntimeException("Erreur lors de la génération du billet : " + e.getMessage());
        }
    }

}






