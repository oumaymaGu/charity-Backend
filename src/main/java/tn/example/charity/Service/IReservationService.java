package tn.example.charity.Service;

import tn.example.charity.Entity.Reservation;

import java.util.List;

public interface IReservationService {
    Reservation ajouterReservation(Long idLog, String username, String message);
    List<Reservation> getAllReservations();
    List<Reservation> getReservationsByUser(Long userId);
    Reservation changerStatut(Long id, String statut, String adminEmail); // Updated to include adminEmail
    void supprimerReservation(Long id);
}