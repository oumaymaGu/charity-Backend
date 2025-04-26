package tn.example.charity.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class GenderDetectionService {

    @Value("${face.api.key}")
    private String apiKey;

    @Value("${face.api.secret}")
    private String apiSecret;

    private final String API_URL = "https://api-us.faceplusplus.com/facepp/v3/detect";
    private final RestTemplate restTemplate = new RestTemplate();

    @Data
    public static class GenderDetectionResult {
        private String gender;
        private Double confidence;

        private Integer age;

        private Double emotionHappiness;
        private Double emotionSadness;
        private Double emotionAnger;
        private Double emotionSurprise;
        private Double emotionFear;
        private Double emotionDisgust;
        private Double emotionNeutral;
    }

    public GenderDetectionResult detectGender(MultipartFile imageFile) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("api_key", apiKey);
        body.add("api_secret", apiSecret);
        body.add("image_file", imageFile.getResource());
        body.add("return_attributes", "gender,age,emotion");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String response = restTemplate.postForObject(API_URL, requestEntity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);

        GenderDetectionResult result = new GenderDetectionResult();

        if (root.has("faces") && root.get("faces").isArray() && root.get("faces").size() > 0) {
            JsonNode face = root.get("faces").get(0);
            JsonNode attributes = face.get("attributes");

            if (attributes != null) {
                // Gender
                JsonNode genderNode = attributes.get("gender");
                String gender = (genderNode != null && genderNode.has("value") && !genderNode.get("value").isNull())
                        ? genderNode.get("value").asText().toLowerCase()
                        : "unknown";
                double genderConfidence = (genderNode != null && genderNode.has("confidence") && !genderNode.get("confidence").isNull())
                        ? genderNode.get("confidence").asDouble(0.0)
                        : 0.0;
                result.setGender(gender);
                result.setConfidence(genderConfidence);

                // Age
                int age = (attributes.has("age") && attributes.get("age").has("value"))
                        ? attributes.get("age").get("value").asInt()
                        : 0;
                result.setAge(age);

                // Emotions
                JsonNode emotionNode = attributes.get("emotion");
                result.setEmotionHappiness(emotionNode != null && emotionNode.has("happiness") ? emotionNode.get("happiness").asDouble(0.0) : 0.0);
                result.setEmotionSadness(emotionNode != null && emotionNode.has("sadness") ? emotionNode.get("sadness").asDouble(0.0) : 0.0);
                result.setEmotionAnger(emotionNode != null && emotionNode.has("anger") ? emotionNode.get("anger").asDouble(0.0) : 0.0);
                result.setEmotionSurprise(emotionNode != null && emotionNode.has("surprise") ? emotionNode.get("surprise").asDouble(0.0) : 0.0);
                result.setEmotionFear(emotionNode != null && emotionNode.has("fear") ? emotionNode.get("fear").asDouble(0.0) : 0.0);
                result.setEmotionDisgust(emotionNode != null && emotionNode.has("disgust") ? emotionNode.get("disgust").asDouble(0.0) : 0.0);
                result.setEmotionNeutral(emotionNode != null && emotionNode.has("neutral") ? emotionNode.get("neutral").asDouble(0.0) : 0.0);
            } else {
                result.setGender("unknown");
                result.setConfidence(0.0);
                result.setAge(0);
            }
        } else {
            result.setGender("unknown");
            result.setConfidence(0.0);
            result.setAge(0);
        }

        return result;
    }
}


