package tn.example.charity.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tn.example.charity.dto.MedicationInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrService implements IOcrService {

    private static final String API_URL = "https://api.ocr.space/parse/image";
    private static final String API_KEY = "helloworld";
    private final RestTemplate restTemplate;

    @Autowired
    public OcrService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Patterns améliorés
    private static final Pattern LOT_PATTERN = Pattern.compile("(?:Lot|LOT|N°|No|Num|Numéro)[\\s:]*([A-Z0-9]{4,})", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXP_DATE_PATTERN = Pattern.compile("(?:EXP|Exp\\.|Expiration|Date exp\\.)[\\s:]*((\\d{2})[\\/](\\d{2,4}))", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_PATTERN = Pattern.compile("\\b(\\d{2}[\\/]\\d{2}[\\/]\\d{2,4}|\\d{2}[\\/]\\d{4})\\b");
    private static final Pattern NAME_PATTERN = Pattern.compile("(?:Nom|Médicament|Produit|Désignation|Marque)[\\s:]*([^\\n]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern PC_PATTERN = Pattern.compile("(?:PC|Code produit)[\\s:]*([0-9]{12,14})", Pattern.CASE_INSENSITIVE);
    private static final Pattern FABRICATION_DATE_PATTERN = Pattern.compile("(?:Fabriqué le|Fab\\.|Date fab\\.|Fabrication)[\\s:]*((\\d{2}[\\/\\-]\\d{2}[\\/\\-]\\d{2,4}))", Pattern.CASE_INSENSITIVE);

    public MedicationInfo extractMedicationInfo(MultipartFile file) throws IOException {
        try {
            String extractedText = extractTextFromImage(file);
            MedicationInfo info = analyzeText(extractedText);

            // Validation de la date d'expiration
            if (info.getExpirationDate() != null) {
                info.setExpirationValid(isExpirationDateValid(info.getExpirationDate()));
            }

            return info;
        } catch (Exception e) {
            throw new IOException("Erreur OCR: " + e.getMessage(), e);
        }
    }

    private MedicationInfo analyzeText(String text) {
        String[] lines = text.split("\\r?\\n");

        String lotNumber = extractLotNumber(text, lines);
        String expirationDate = extractExpirationDate(text, lines);
        String fabricationDate = extractFabricationDate(text, lines);
        String medicationName = extractMedicationName(text, lines);
        String productCode = extractProductCode(text, lines);

        return new MedicationInfo(
                medicationName != null ? medicationName : "Non détecté",
                expirationDate != null ? expirationDate : "Non détectée",
                lotNumber != null ? lotNumber : "Non détecté",
                productCode != null ? productCode : "Non détecté",
                fabricationDate != null ? fabricationDate : "Non détectée",
                text,
                false // Valeur par défaut, sera mise à jour après validation
        );
    }

    private String extractLotNumber(String fullText, String[] lines) {
        Matcher matcher = LOT_PATTERN.matcher(fullText);
        if (matcher.find()) {
            String lot = matcher.group(1).trim();
            if (isValidLotNumber(lot)) return lot;
        }

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].matches("(?i).*Lot\\s*n°?\\s*[:]?.*") && i + 1 < lines.length) {
                String potentialLot = lines[i + 1].trim();
                if (isValidLotNumber(potentialLot)) return potentialLot;
            }
        }

        Matcher digitMatcher = Pattern.compile("\\b\\d{4,}\\b").matcher(fullText);
        if (digitMatcher.find()) return digitMatcher.group();

        return null;
    }

    private boolean isValidLotNumber(String lot) {
        return lot != null && lot.matches("[A-Z0-9]{4,}");
    }

    private String extractExpirationDate(String fullText, String[] lines) {
        // Recherche du format EXP: MM/YYYY
        Matcher expMatcher = EXP_DATE_PATTERN.matcher(fullText);
        if (expMatcher.find()) {
            return expMatcher.group(1);
        }

        // Recherche d'autres formats de date
        Matcher dateMatcher = DATE_PATTERN.matcher(fullText);
        if (dateMatcher.find()) {
            return dateMatcher.group();
        }

        // Recherche dans les lignes
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].matches("(?i).*Exp\\.?\\s*[:]?.*") && i + 1 < lines.length) {
                String potentialDate = lines[i + 1].trim();
                if (potentialDate.matches("\\d{2}/\\d{2,4}")) {
                    return potentialDate;
                }
            }
        }

        return null;
    }

    public boolean isExpirationDateValid(String expirationDateStr) {
        if (expirationDateStr == null || expirationDateStr.isEmpty()) {
            return false;
        }

        try {
            String[] parts = expirationDateStr.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = parts[1].length() == 2 ? 2000 + Integer.parseInt(parts[1]) : Integer.parseInt(parts[1]);

            LocalDate expirationDate = LocalDate.of(year, month, 1)
                    .with(TemporalAdjusters.lastDayOfMonth());
            LocalDate minValidDate = LocalDate.now().plusYears(1);

            return !expirationDate.isBefore(minValidDate);
        } catch (Exception e) {
            return false;
        }
    }




    private String extractFabricationDate(String fullText, String[] lines) {
        Matcher matcher = FABRICATION_DATE_PATTERN.matcher(fullText);
        if (matcher.find()) return matcher.group(1).trim();

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].matches("(?i).*Fabrication.*") && i + 1 < lines.length) {
                String potentialDate = lines[i + 1].trim();
                if (potentialDate.matches("\\d{2}[\\/\\-]\\d{2}[\\/\\-]\\d{2,4}")) return potentialDate;
            }
        }

        return null;
    }

    private String extractMedicationName(String fullText, String[] lines) {
        Matcher matcher = NAME_PATTERN.matcher(fullText);
        if (matcher.find()) return matcher.group(1).trim();

        for (String line : lines) {
            if (line.matches(".*[A-Za-zÀ-ÿ].*") &&
                    line.matches(".*\\d.*") &&
                    line.length() > 5 &&
                    !line.matches(".*(Lot|Exp|PC|Date).*")) {
                return line.trim();
            }
        }

        for (String line : lines) {
            if (line.matches("[A-Za-zÀ-ÿ]{4,}")) return line.trim();
        }

        return null;
    }

    private String extractProductCode(String fullText, String[] lines) {
        Matcher matcher = PC_PATTERN.matcher(fullText);
        if (matcher.find()) return matcher.group(1);

        Matcher digitMatcher = Pattern.compile("\\b\\d{12,14}\\b").matcher(fullText);
        if (digitMatcher.find()) return digitMatcher.group();

        return null;
    }

    private String extractTextFromImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Le fichier est vide");
        if (file.getSize() > 5_000_000) throw new IllegalArgumentException("Fichier trop volumineux (>5MB)");

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

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return extractTextFromJson(response.getBody());
            } else {
                throw new IOException("API returned status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new IOException("Erreur de communication avec l'API OCR: " + e.getMessage(), e);
        }
    }

    private String extractTextFromJson(String jsonResponse) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(jsonResponse)
                    .path("ParsedResults")
                    .get(0)
                    .path("ParsedText")
                    .asText()
                    .replaceAll("(?m)^\\s*$[\n\r]+", "")
                    .replaceAll("\\s+", " ")
                    .trim();
        } catch (Exception e) {
            throw new IOException("Erreur d'analyse de la réponse JSON", e);
        }
    }
}
