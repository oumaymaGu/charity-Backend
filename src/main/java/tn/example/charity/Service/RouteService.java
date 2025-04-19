package tn.example.charity.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RouteService {

    @Autowired
    private RestTemplate restTemplate;

    private final String apiKey = "5b3ce3597851110001cf624812980a7e3e7446c4b6be3ba05dda1040"; // Remplace par ta clé OpenRouteService

    public String getTravelTime(double startLat, double startLon, double endLat, double endLon) {
        String url = "https://api.openrouteservice.org/v2/directions/driving-car";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("""
            {
              "coordinates":[[%f,%f],[%f,%f]]
            }
            """, startLon, startLat, endLon, endLat);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response.getBody(); // Tu peux parser le JSON ensuite
    }
}