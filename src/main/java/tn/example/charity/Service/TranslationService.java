package tn.example.charity.Service;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Service
public class TranslationService {

    public String translate(String text, String source, String target) {
        String jsonInput = String.format(
                "{\"q\":\"%s\",\"source\":\"%s\",\"target\":\"%s\",\"format\":\"text\"}",
                text, source, target
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://translate.argosopentech.com/translate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                .build();
        System.out.println(request);
        System.out.println(jsonInput);
        System.out.println(HttpResponse.BodyHandlers.ofString().toString());
        HttpClient client = HttpClient.newHttpClient();
        try {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            String json = response.body();
            int start = json.indexOf(":\"") + 2;
            int end = json.indexOf("\"}", start);
            return json.substring(start, end);

        } catch (Exception e) {
            e.printStackTrace(); // Affiche la vraie cause dans la console

            return "Erreur : " + e.getClass().getSimpleName();
        }

    }
}