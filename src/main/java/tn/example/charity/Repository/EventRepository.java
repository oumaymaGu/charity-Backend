package tn.example.charity.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Event;
import tn.example.charity.Entity.User;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findEventByNomEvent(String nomEvent);

    Event findEventByIdEvent(long idEvent);
    @Query("SELECT u FROM User u JOIN u.events e WHERE e.idEvent = :eventId")
    List<User> findUsersByEventId(Long eventId);
}