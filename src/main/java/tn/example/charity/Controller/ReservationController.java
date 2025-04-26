package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Reservation;
import tn.example.charity.Entity.User;
import tn.example.charity.Repository.UserRepository;
import tn.example.charity.Service.IReservationService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*") // Or specify your Angular app's URL
public class ReservationController {

    @Autowired
    private IReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> ajouterReservation(@RequestBody Map<String, Object> request) {
        try {
            Long logementId = Long.parseLong(request.get("logementId").toString());
            String username = request.get("username").toString();
            String message = request.get("message") != null ? request.get("message").toString() : "";

            Reservation reservation = reservationService.ajouterReservation(logementId, username, message);
            return new ResponseEntity<>(reservation, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getReservationsByUser(@PathVariable String username) {
        try {
            Optional<User> user = userRepository.findByUsername(username);

            if (user.isPresent()) {
                List<Reservation> reservations = reservationService.getReservationsByUser(user.get().getIdUser());
                return new ResponseEntity<>(reservations, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("message", "User not found"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> changerStatut(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        try {
            String statut = statusMap.get("statut");
            String adminEmail = statusMap.get("adminEmail"); // Get the admin email from request

            if (statut == null) {
                return new ResponseEntity<>(Map.of("message", "Status is required"), HttpStatus.BAD_REQUEST);
            }

            if (!statut.equals("ACCEPTEE") && !statut.equals("REFUSEE") && !statut.equals("EN_ATTENTE")) {
                return new ResponseEntity<>(Map.of("message", "Invalid status value. Must be ACCEPTEE, REFUSEE, or EN_ATTENTE"),
                        HttpStatus.BAD_REQUEST);
            }

            Reservation updatedReservation = reservationService.changerStatut(id, statut, adminEmail);
            return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerReservation(@PathVariable Long id) {
        try {
            reservationService.supprimerReservation(id);
            return new ResponseEntity<>(Map.of("message", "Reservation deleted successfully"), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}