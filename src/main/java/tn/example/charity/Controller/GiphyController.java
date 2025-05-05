package tn.example.charity.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Service.GiphyService;
import tn.example.charity.dto.GiphyResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/giphy")
@CrossOrigin(origins = "http://localhost:4200")
public class GiphyController {

    @Autowired
    private GiphyService giphyService;

    @GetMapping("/search")
    public List<String> searchGifs(@RequestParam String query) {
        GiphyResponse response = giphyService.searchGifs(query);
        return response.getData().stream()
                .map(data -> data.getImages().getOriginal().getUrl())
                .collect(Collectors.toList()); // Liste des URLs des GIFs
    }
}

