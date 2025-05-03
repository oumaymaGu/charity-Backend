package tn.example.charity.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tn.example.charity.Entity.TranslationRequest;
import tn.example.charity.Service.TranslationService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/translate")
@CrossOrigin(origins = "http://localhost:4200") // Pour autoriser les appels Angular
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @GetMapping
    public String translate(@RequestParam String word,
                            @RequestParam(defaultValue = "fr") String source,
                            @RequestParam(defaultValue = "en") String target) {
        return translationService.translate(word, source, target);
    } }