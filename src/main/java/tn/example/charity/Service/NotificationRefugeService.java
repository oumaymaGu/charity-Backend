package tn.example.charity.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.NotificationRefuge;
import tn.example.charity.Repository.NotificationRefugeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationRefugeService implements INotificationRefugeService {

    @Autowired
    private NotificationRefugeRepository notificationRefugeRepository;

    @Override
    public NotificationRefuge save(String message) {
        NotificationRefuge notification = NotificationRefuge.builder()
                .message(message)
                .date(LocalDateTime.now())
                .lu(false)  // Marque la notification comme non lue par défaut
                .build();
        return notificationRefugeRepository.save(notification);
    }

    @Override
    public List<NotificationRefuge> getAll() {
        return notificationRefugeRepository.findAll();
    }

    @Override
    public List<NotificationRefuge> getUnreadNotifications() {
        return notificationRefugeRepository.findByLu(false);  // Récupère les notifications non lues
    }

    @Override
    public Optional<NotificationRefuge> markAsRead(Long id) {
        Optional<NotificationRefuge> notification = notificationRefugeRepository.findById(id);
        notification.ifPresent(n -> {
            n.setLu(true);  // Marque la notification comme lue
            notificationRefugeRepository.save(n);
        });
        return notification;
    }

    @Override
    public Optional<NotificationRefuge> getNotificationById(Long id) {
        return notificationRefugeRepository.findById(id);  // Récupère une notification par son ID
    }

    // Méthode pour envoyer une notification
    @Override
    public NotificationRefuge sendNotification(String message) {
        NotificationRefuge notification = NotificationRefuge.builder()
                .message(message)
                .date(LocalDateTime.now())  // Date actuelle de la notification
                .lu(false)  // Notification non lue par défaut
                .build();
        return notificationRefugeRepository.save(notification);  // Sauvegarde dans la base de données
    }
}
