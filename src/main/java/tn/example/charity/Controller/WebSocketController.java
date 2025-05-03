package tn.example.charity.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import tn.example.charity.Entity.DeliveryLocation;
import tn.example.charity.Service.LivraisonServiceImpl;

@Controller
public class WebSocketController {

    @Autowired
    private LivraisonServiceImpl livraisonService;

    @MessageMapping("/updateLocation")
    public void updateLocation(@Payload DeliveryLocation location) {
        livraisonService.updateDriverLocation(
                location.getLivraisonId(),
                location.getLatitude(),
                location.getLongitude()
        );
    }
}
