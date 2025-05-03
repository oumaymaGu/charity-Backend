package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tn.example.charity.Entity.DeliveryLocation;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.Livraisons;
import tn.example.charity.Service.ILivraisonService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.List;

@RestController
@AllArgsConstructor

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/livraison")
public class LivraisonsRestController {
    @Autowired
    private ILivraisonService livraisonService;
    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/add-livraison")
    public Livraisons addLivraison(@RequestBody Livraisons l) {
        Livraisons livraison = livraisonService.addLivraison(l);
        return livraison;

    }

    @DeleteMapping("/remove-livraison/{livraison-id}")
    public void removeLivraison(@PathVariable("livraison-id") Long livraison) {

        livraisonService.deleteLivraison(livraison);
    }

    @PutMapping("/modifyLivraison")
    public Livraisons modifyLivraison(@RequestBody Livraisons l) {
        Livraisons livraison = livraisonService.modifyLivraison(l);
        return livraison;
    }

    @GetMapping("/retrieve-all-Livraison")

    public List<Livraisons> getLivraisons() {
        List<Livraisons> listLivraisons = livraisonService.getAllLivraison();
        return listLivraisons;

    }

    @GetMapping("/get-livraison/{livraison-id}")
    public Livraisons getlivraison(@PathVariable("livraison-id") Long l) {
        Livraisons livraison = livraisonService.retrieveallLivraisonbyid(l);
        return livraison;
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportLivraisonsToPDF() {
        byte[] pdfContent = livraisonService.generatePDF();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "livraisons.pdf");
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @GetMapping("/get-random-image")
    public String getRandomImage(@RequestParam String query) {
        String accessKey = "Rz9k_4me1x68VA7bvCw-hRlIeSOma6XUwi195ZCb39k"; // Remplace ici par ta vraie clé API
        try {
            String apiUrl = "https://api.unsplash.com/photos/random?query=" + query + "&client_id=" + accessKey;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(content.toString());
            String imageUrl = json.getJSONObject("urls").getString("small");
            return imageUrl;

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la récupération de l'image";
        }
    }
    @PostMapping("/enable-tracking/{livraison-id}")
    public ResponseEntity<Void> enableTracking(@PathVariable("livraison-id") Long livraisonId) {
        livraisonService.enableTracking(livraisonId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/update-location/{livraison-id}")
    public ResponseEntity<Void> updateDriverLocation(
            @PathVariable("livraison-id") Long livraisonId,
            @RequestBody DeliveryLocation location) {
        livraisonService.updateDriverLocation(livraisonId, location.getLatitude(), location.getLongitude());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/estimate-time")
    public ResponseEntity<String> estimateDeliveryTime(@RequestBody Map<String, String> addresses) {
        String origin = addresses.get("origin");
        String destination = addresses.get("destination");

        if (origin == null || origin.trim().isEmpty() || destination == null || destination.trim().isEmpty()) {
            return new ResponseEntity<>("Les adresses d'origine et de destination sont requises", HttpStatus.BAD_REQUEST);
        }

        try {
            String accessToken = "pk.eyJ1IjoiaGFkaGVtaTIyIiwiYSI6ImNtYTVreG14OTBqY3QyaXF6M3lpcnAxZmMifQ.oEbvPz7gb6TvjwW07jEYXg";
            System.out.println("Starting estimation from: " + origin + " to: " + destination);

            // Geocoding pour l'origine
            String originUrl = "https://api.mapbox.com/geocoding/v5/mapbox.places/" + URLEncoder.encode(origin, StandardCharsets.UTF_8) + ".json?access_token=" + accessToken;
            System.out.println("Geocoding origin URL: " + originUrl);
            URL url = new URL(originUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();
            String originResponse = content.toString();
            System.out.println("Origin geocoding response: " + originResponse);
            JSONObject originJson = new JSONObject(originResponse);
            JSONArray originFeatures = originJson.getJSONArray("features");
            if (originFeatures.length() == 0) {
                return new ResponseEntity<>("Adresse d'origine introuvable: " + origin, HttpStatus.BAD_REQUEST);
            }
            JSONArray originCoords = originFeatures.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");

            // Geocoding pour la destination
            String destUrl = "https://api.mapbox.com/geocoding/v5/mapbox.places/" + URLEncoder.encode(destination, StandardCharsets.UTF_8) + ".json?access_token=" + accessToken;
            System.out.println("Geocoding destination URL: " + destUrl);
            url = new URL(destUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();
            String destResponse = content.toString();
            System.out.println("Destination geocodage response: " + destResponse);
            JSONObject destJson = new JSONObject(destResponse);
            JSONArray destFeatures = destJson.getJSONArray("features");
            if (destFeatures.length() == 0) {
                return new ResponseEntity<>("Adresse de destination introuvable: " + destination, HttpStatus.BAD_REQUEST);
            }
            JSONArray destCoords = destFeatures.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");

            // Directions API
            String directionsUrl = "https://api.mapbox.com/directions/v5/mapbox/driving/" +
                    originCoords.getDouble(0) + "," + originCoords.getDouble(1) + ";" +
                    destCoords.getDouble(0) + "," + destCoords.getDouble(1) +
                    "?access_token=" + accessToken + "&alternatives=false";
            System.out.println("Directions API URL: " + directionsUrl);
            url = new URL(directionsUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();
            String directionsResponse = content.toString();
            System.out.println("Directions API response: " + directionsResponse);
            JSONObject directionsJson = new JSONObject(directionsResponse);
            JSONArray routes = directionsJson.getJSONArray("routes");
            if (routes.length() == 0) {
                return new ResponseEntity<>("Impossible de calculer le trajet", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            double durationSeconds = routes.getJSONObject(0).getDouble("duration"); // Durée en secondes
            System.out.println("Raw duration from Directions API: " + durationSeconds + " seconds");
            int totalMinutes = (int) Math.round(durationSeconds / 60); // Conversion en minutes
            System.out.println("Converted duration: " + totalMinutes + " minutes");
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;
            String estimatedTime = hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
            System.out.println("Final estimated time: " + estimatedTime);
            return new ResponseEntity<>(estimatedTime, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception caught: " + e.getClass().getName() + " - " + e.getMessage());
            return new ResponseEntity<>("Erreur lors de l'estimation: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}