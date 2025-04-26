package tn.example.charity.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.example.charity.Entity.*;
import tn.example.charity.Repository.NotificationRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void createAndSendDonNotification(Don donation) {
        String message = donation.getTypeDon() == TypeDon.MATERIEL
                ? String.format("Nouveau don matériel: %s",
                donation.getCategory() != null ? donation.getCategory() : "Non spécifié")
                : String.format("Nouveau don financier: %.2f€", donation.getAmount());

        String type = donation.getTypeDon() == TypeDon.MATERIEL
                ? "DON_MATERIEL"
                : "DON_FINANCIER";

        createAndSendNotification(message, type, donation, null);
    }

    @Override
    @Transactional
    public void createAndSendStripeNotification(StripePayment stripePayment) {
        String message = String.format("Nouveau paiement Stripe: %.2f%s (Status: %s)",
                stripePayment.getAmount(),
                stripePayment.getCurrency(),
                stripePayment.getStatus());

        createAndSendNotification(message, "STRIPE_PAYMENT", null, stripePayment);
    }

    private void createAndSendNotification(String message, String type,
                                           Don donation, StripePayment stripePayment) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(type);
        notification.setTimestamp(new Date());
        notification.setRead(false);
        notification.setDonation(donation);
        notification.setStripePayment(stripePayment);

        Notification savedNotification = notificationRepository.save(notification);
        sendWebSocketNotification(savedNotification);
    }

    private void sendWebSocketNotification(Notification notification) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", notification.getId());
        payload.put("message", notification.getMessage());
        payload.put("type", notification.getType());
        payload.put("timestamp", notification.getTimestamp());
        payload.put("isRead", notification.isRead());

        if (notification.getDonation() != null) {
            payload.put("entityId", notification.getDonation().getIdDon());
        } else if (notification.getStripePayment() != null) {
            payload.put("entityId", notification.getStripePayment().getId());
        }

        messagingTemplate.convertAndSend("/topic/notifications", payload);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllByOrderByTimestampDesc();
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Override
    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalseOrderByTimestampDesc();
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}