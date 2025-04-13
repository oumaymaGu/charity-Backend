package tn.example.charity.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tn.example.charity.dto.MedicationInfo;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrService implements IOcrService {
    private static final String API_URL = "https://api.ocr.space/parse/image";
    private static final String API_KEY = "helloworld";

    private static final Pattern LOT_PATTERN = Pattern.compile(
            "(?:Lot|LOT|N°|No|Num|Numéro|Loir)[\\s:]*([A-Z0-9]{4,})",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "\\b(\\d{2}[\\/]\\d{2}[\\/]\\d{2,4}|\\d{2}[\\/]\\d{4})\\b"
    );
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "(?:Nom|Médicament|Produit|Désignation|Marque)[\\s:]*([^\\n]+)",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern PC_PATTERN = Pattern.compile(
            "PC[\\s:]*([0-9]{12,14})",
            Pattern.CASE_INSENSITIVE
    );

    public MedicationInfo extractMedicationInfo(MultipartFile file) throws IOException {
        String extractedText = extractTextFromImage(file);
        return analyzeText(extractedText);
    }

    private MedicationInfo analyzeText(String text) {
        String[] lines = text.split("\\r?\\n");

        String lotNumber = extractLotNumber(text, lines);
        String expirationDate = extractExpirationDate(text, lines);
        String medicationName = extractMedicationName(text, lines);
        String productCode = extractProductCode(text, lines);

        return new MedicationInfo(
                medicationName != null ? medicationName : "Non détecté",
                expirationDate != null ? expirationDate : "Non détectée",
                lotNumber != null ? lotNumber : "Non détecté",
                productCode != null ? productCode : "Non détecté",
                text
        );
    }

    private String extractLotNumber(String fullText, String[] lines) {
        Matcher matcher = LOT_PATTERN.matcher(fullText);
        if (matcher.find()) {
            String lot = matcher.group(1).trim();
            if (isValidLotNumber(lot)) {
                return lot;
            }
        }

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].matches("(?i).*Lot\\s*n°?\\s*[:]?.*")) {
                if (i + 1 < lines.length) {
                    String potentialLot = lines[i + 1].trim();
                    if (isValidLotNumber(potentialLot)) {
                        return potentialLot;
                    }
                }
            }
        }

        Matcher digitMatcher = Pattern.compile("\\b\\d{4,}\\b").matcher(fullText);
        if (digitMatcher.find()) {
            return digitMatcher.group();
        }

        return null;
    }

    private boolean isValidLotNumber(String lot) {
        return lot != null && lot.matches("[A-Z0-9]{4,}");
    }

    private String extractExpirationDate(String fullText, String[] lines) {
        Matcher matcher = DATE_PATTERN.matcher(fullText);
        if (matcher.find()) {
            return matcher.group();
        }

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].matches("(?i).*Exp\\.?\\s*[:]?.*")) {
                if (i + 1 < lines.length) {
                    String potentialDate = lines[i + 1].trim();
                    if (potentialDate.matches("\\d{2}/\\d{2,4}")) {
                        return potentialDate;
                    }
                }
            }
        }

        return null;
    }

    private String extractMedicationName(String fullText, String[] lines) {
        Matcher matcher = NAME_PATTERN.matcher(fullText);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        for (String line : lines) {
            if (line.matches(".*[A-Za-zÀ-ÿ].*") &&
                    line.matches(".*\\d.*") &&
                    line.length() > 5 &&
                    !line.matches(".*(Lot|Exp|PC|Date).*")) {
                return line.trim();
            }
        }

        for (String line : lines) {
            if (line.matches("[A-Za-zÀ-ÿ]{4,}")) {
                return line.trim();
            }
        }

        return null;
    }

    private String extractProductCode(String fullText, String[] lines) {
        Matcher matcher = PC_PATTERN.matcher(fullText);
        if (matcher.find()) {
            return matcher.group(1);
        }

        Matcher digitMatcher = Pattern.compile("\\b\\d{12,14}\\b").matcher(fullText);
        if (digitMatcher.find()) {
            return digitMatcher.group();
        }

        return null;
    }

    private String extractTextFromImage(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("apikey", API_KEY);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        body.add("language", "fre");
        body.add("isOverlayRequired", "false");
        body.add("OCREngine", "2");
        body.add("scale", "true");
        body.add("detectOrientation", "true");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 🔁 Configuration du RestTemplate avec timeout
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000); // 30 secondes
        requestFactory.setReadTimeout(60000);      // 60 secondes

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        System.out.println("Tentative de connexion à l'API OCR...");

        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, requestEntity, String.class);

        return cleanOcrText(extractTextFromJson(response.getBody()));
    }

    private String extractTextFromJson(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);
            return rootNode.path("ParsedResults")
                    .get(0)
                    .path("ParsedText")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'analyse de la réponse OCR", e);
        }
    }

    private String cleanOcrText(String text) {
        return text.replaceAll("(?m)^\\s*$[\n\r]+", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

}
