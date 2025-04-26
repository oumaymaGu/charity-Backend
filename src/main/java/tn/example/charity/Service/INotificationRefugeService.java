package tn.example.charity.Service;

import tn.example.charity.Entity.NotificationRefuge;

import java.util.List;
import java.util.Optional;

public interface INotificationRefugeService {
    // Enregistrer une nouvelle notification
    NotificationRefuge save(String message);

    // Récupérer toutes les notifications
    List<NotificationRefuge> getAll();

    // Récupérer les notifications non lues
    List<NotificationRefuge> getUnreadNotifications();

    // Marquer une notification comme lue
    Optional<NotificationRefuge> markAsRead(Long id);

    // Récupérer une notification par son ID
    Optional<NotificationRefuge> getNotificationById(Long id);
    NotificationRefuge sendNotification(String message);
}
