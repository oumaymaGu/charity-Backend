package tn.example.charity.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.example.charity.dto.GiphyResponse;

@Service
public class GiphyService {

    private final String apiKey = "K5OgRdbryviEETR6qWhBM0wZEX5TG0TO";

    public GiphyResponse searchGifs(String query) {
        String url = "https://api.giphy.com/v1/gifs/search?api_key=" + apiKey + "&q=" + query + "&limit=10";

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, GiphyResponse.class); // Désérialisation en GiphyResponse
    }
}
