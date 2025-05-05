package tn.example.charity.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONObject;
import tn.example.charity.dto.WeatherInfo;

import java.time.LocalDate;

@Service
public class WeatherService {

    private final String API_KEY = "f35c9187f385cda0f410352c0b18d602";

    public WeatherInfo getWeather(double lat, double lon, LocalDate date) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.openweathermap.org/data/2.5/forecast")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", API_KEY)
                .queryParam("units", "metric")
                .queryParam("lang", "fr")
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);

        double temperature = json.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp");
        String description = json.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");

        return new WeatherInfo(temperature, description);
    }
}
