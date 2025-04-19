package tn.example.charity.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import tn.example.charity.Entity.Location;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Méthode pour envoyer la location aux clients abonnés
    @MessageMapping("/location")
    public void sendLocation(Location location) {
        // Envoie de la location à tous les abonnés de "/topic/location"
        messagingTemplate.convertAndSend("/topic/location", location);
    }
}