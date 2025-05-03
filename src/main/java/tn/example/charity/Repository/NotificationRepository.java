package tn.example.charity.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.example.charity.Entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
        List<Notification> findByIsReadFalseOrderByTimestampDesc();
        List<Notification> findAllByOrderByTimestampDesc();
        boolean existsByMessageAndTypeAndDonation_IdDon(String message, String type, Long idDon);

    }

