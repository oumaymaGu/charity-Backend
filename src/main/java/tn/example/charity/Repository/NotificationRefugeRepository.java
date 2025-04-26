package tn.example.charity.Repository;



import org.springframework.data.jpa.repository.JpaRepository;
import tn.example.charity.Entity.NotificationRefuge;

import java.util.List;

public interface NotificationRefugeRepository extends JpaRepository<NotificationRefuge, Long> {
    List<NotificationRefuge> findByLu(boolean lu);
}

