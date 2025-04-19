package tn.example.charity.Service;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class TranslationService {

    public String translate(String text, String targetLang) {
        String apiUrl = "https://libretranslate.com/translate";
        String translatedText = "";

        // Configuration des timeouts
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .build();

        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build()) {

            HttpPost post = new HttpPost(apiUrl);

            JSONObject requestBody = new JSONObject();
            requestBody.put("q", text);
            requestBody.put("source", "fr");
            requestBody.put("target", targetLang);
            requestBody.put("format", "text");

            // Afficher la requête envoyée pour débogage
            System.out.println("Requête envoyée : " + requestBody.toString());
            post.setEntity(new StringEntity(requestBody.toString(), "UTF-8"));
            post.setHeader("Content-Type", "application/json");

            HttpResponse response = client.execute(post);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String result = EntityUtils.toString(responseEntity);

                // Afficher la réponse brute pour débogage
                System.out.println("=== RAW RESPONSE ===");
                System.out.println(result);

                // Extraire le texte traduit de la réponse
                JSONObject json = new JSONObject(result);
                if (json.has("translatedText")) {
                    translatedText = json.getString("translatedText");
                } else {
                    translatedText = "[Aucune traduction reçue]";
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de la requête HTTP vers l'API :");
            e.printStackTrace();
        }

        System.out.println("Texte traduit reçu : " + translatedText);
        return translatedText;
    }
}
