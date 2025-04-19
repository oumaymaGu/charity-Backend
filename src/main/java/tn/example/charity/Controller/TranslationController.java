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

    @PostMapping
    public String translate(@RequestBody Map<String, String> request) {
        System.out.println("Requête reçue : " + request);
        String text = request.get("text");
        String targetLang = request.get("targetLang");
        System.out.println("Texte : " + text + " | Langue cible : " + targetLang);
        return translationService.translate(text, targetLang);
    }
    public void testTranslation() {
        String result = translationService.translate("Bonjour tout le monde", "en");
        System.out.println("Résultat traduit : " + result);
    }


}
