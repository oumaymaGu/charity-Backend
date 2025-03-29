package tn.example.charity.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("SELECT e FROM Event e WHERE " +
            "(6371 * acos(cos(radians(:userLat)) * cos(radians(e.latitude)) * cos(radians(e.longitude) - radians(:userLon)) + sin(radians(:userLat)) * sin(radians(e.latitude)))) <= :radius")
    List<Event> findEventsNear(@Param("userLat") double userLat,
                               @Param("userLon") double userLon,
                               @Param("radius") double radius);



}