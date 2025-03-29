package tn.example.charity.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONObject;

@Service
public class GeocodingService {

    private final String API_KEY = "774a8d49846541c1bd53147aa2dc74f7"; // Remplace par ta clé OpenCage

    public double[] getCoordinates(String city) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.opencagedata.com/geocode/v1/json")
                .queryParam("q", city)
                .queryParam("key", API_KEY)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);

        double lat = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getDouble("lat");
        double lon = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getDouble("lng");

        return new double[]{lat, lon};
    }
}
