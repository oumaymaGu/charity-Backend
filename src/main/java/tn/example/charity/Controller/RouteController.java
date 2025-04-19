package tn.example.charity.Controller;


import tn.example.charity.Service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/duration")
    public ResponseEntity<String> getDuration(
            @RequestParam double startLat,
            @RequestParam double startLon,
            @RequestParam double endLat,
            @RequestParam double endLon
    ) {
        String travelTime = routeService.getTravelTime(startLat, startLon, endLat, endLon);
        return ResponseEntity.ok(travelTime);
    }
}