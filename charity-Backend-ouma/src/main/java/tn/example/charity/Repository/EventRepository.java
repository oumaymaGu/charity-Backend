package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
