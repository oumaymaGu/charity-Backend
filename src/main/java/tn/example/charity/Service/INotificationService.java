package tn.example.charity.Service;

import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.Notification;
import tn.example.charity.Entity.StripePayment;

import java.util.List;

public interface INotificationService {
    /**
     * Crée et envoie une notification pour un don
     * @param donation Le don associé à la notification
     */
    void createAndSendDonNotification(Don donation);

    /**
     * Crée et envoie une notification pour un paiement Stripe
     * @param stripePayment Le paiement Stripe associé
     */
    void createAndSendStripeNotification(StripePayment stripePayment);

    /**
     * Récupère toutes les notifications
     * @return Liste des notifications
     */
    List<Notification> getAllNotifications();

    /**
     * Marque une notification comme lue
     * @param notificationId ID de la notification
     */
    void markAsRead(Long notificationId);

    /**
     * Récupère les notifications non lues
     * @return Liste des notifications non lues
     */
    List<Notification> getUnreadNotifications();

    /**
     * Supprime une notification
     * @param notificationId ID de la notification
     */
    void deleteNotification(Long notificationId);
}