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

    private static final Pattern LOT_PATTERN = Pattern.compile("(?:Lot|LOT|N°|No|Num|Numéro)[\\s:]*([A-Z0-9]{4,})", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXP_DATE_PATTERN = Pattern.compile("(?:EXP|Exp\\.|Expiration|Date exp\\.)[\\s:]*((\\d{2})[\\/](\\d{2,4}))", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_PATTERN = Pattern.compile("\\b(\\d{2}[\\/]\\d{2}[\\/]\\d{2,4}|\\d{2}[\\/]\\d{4})\\b");
    private static final Pattern PC_PATTERN = Pattern.compile("(?:PC|Code produit)[\\s:]*([0-9]{12,14})", Pattern.CASE_INSENSITIVE);
    private static final Pattern FABRICATION_DATE_PATTERN = Pattern.compile("(?:Fabriqué le|Fab\\.|Date fab\\.|Fabrication)[\\s:]*((\\d{2}[\\/\\-]\\d{2}[\\/\\-]\\d{2,4}))", Pattern.CASE_INSENSITIVE);

    public MedicationInfo extractMedicationInfo(MultipartFile file) throws IOException {
        try {
            String extractedText = extractTextFromImage(file);
            MedicationInfo info = analyzeText(extractedText);

            if (info.getExpirationDate() != null) {
                boolean isValid = isExpirationDateValid(info.getExpirationDate());
                info.setExpirationValid(isValid);
            } else {
                info.setExpirationValid(false);
            }

            return info;
        } catch (Exception e) {
            throw new IOException("Erreur OCR: " + e.getMessage(), e);
        }
    }

    private MedicationInfo analyzeText(String text) {
        String medicationName = extractMedicationName(text);
        String[] lines = text.split("\\r?\\n");
        String lotNumber = extractLotNumber(text, lines);
        String expirationDate = extractExpirationDate(text, lines);
        String fabricationDate = extractFabricationDate(text, lines);
        String productCode = extractProductCode(text, lines);

        return new MedicationInfo(
                medicationName != null ? medicationName : "Non détecté",
                expirationDate != null ? expirationDate : "Non détectée",
                lotNumber != null ? lotNumber : "Non détecté",
                productCode != null ? productCode : "Non détecté",
                fabricationDate != null ? fabricationDate : "Non détectée",
                text,
                false
        );
    }

    private String extractMedicationName(String fullText) {
        // Diviser le texte en lignes pour isoler le nom
        String[] lines = fullText.split("\\r?\\n");

        // Première tentative : chercher une ligne qui ressemble à un nom de médicament
        // (contient des lettres, éventuellement des chiffres, mais pas de mots-clés comme "lot", "exp", "fab")
        Pattern namePattern = Pattern.compile("(?i)^(?!.*(?:lot|exp|fab|fabrication|ppt|prix|valable|pharmacie|الصيدلية|الثمن)).*[a-zA-ZÀ-ÿ].*");
        for (String line : lines) {
            if (namePattern.matcher(line).find()) {
                System.out.println("Ligne potentielle pour le nom : " + line);
                String cleanedName = cleanLine(line);
                if (!cleanedName.isEmpty()) {
                    return cleanedName;
                }
            }
        }

        // Deuxième tentative : nettoyer le texte complet si aucune ligne n'est trouvée
        String cleanedText = fullText;
        String[] patternsToRemove = {
                "(?i)(?:lot|l.n|lot\\.|n°|no|num|numéro)[\\s:]*[a-zA-Z0-9\\s]{4,}", // Numéro de lot
                "(?i)(?:exp|expiration|date exp\\.|date d'expiration|valable jusqu'à|use by|best before)[\\s:]*(\\d{2}/\\d{2,4}(?:/\\d{4})?)", // Date d'expiration
                "(?i)(?:fab|fabrication|date fab\\.|date de fabrication|fabriqué le|mfg|manufactured)[\\s:]*(\\d{2}/\\d{4}|\\d{2}/\\d{2}/\\d{4})", // Date de fabrication
                "(?i)(?:pc|code produit|code prod\\.|ean|upc)[\\s:]*\\d{12,14}", // Code produit
                "(?i)ppt[\\s:]*\\d+,\\d+\\s*(dt|eur|usd)", // Prix
                "(?i)\\d+\\s*(comprimés?|capsules?|gélules?|sachets?|flacons?|tablets?|pills?)(?:\\s*effervescents?)?", // Quantité et forme
                "(?i)\\s*effervescent(e)?s?", // Effervescent
                "(?i)\\s*\\d+\\s*(ml|mg|g|kg)[\\s]*", // Unités de dosage
                "(?i)made in [a-zA-ZÀ-ÿ]+", // Made in
                "(?i)[a-zA-ZÀ-ÿ]+ (laboratories|laboratoire|pharma|pharmaceuticals)", // Laboratoires
                "(?i)fl\\s*\\d+\\s*ml", // "FL 25 ml"
                "(?i)\\d{1,4}\\s*-\\s*\\d{1,4}\\s*(ou|uuo)?\\s*\\d{2}\\s*\\d{3}\\s*\\d{3}", // Numéros de téléphone
                "(?i)pharmacie.*|el raed.*|el malek.*|prix.*|pharmacy.*|drugstore.*|الصيدلية.*|الملك.*|الثمن.*", // Textes indésirables
                "(?i)\\b\\d{1,4}\\b", // Numéros isolés
                "[^a-zA-ZÀ-ÿ\\s\\d]", // Caractères non désirés
                "(?i)(?:for|pour) (external|oral|intravenous) use", // Usage (ex: "For external use")
                "(?i)batch.*|serial.*|s/n.*", // Batch ou serial
                "(?i)keep out of reach.*|conserver hors.*", // Instructions de stockage
                "(?i)distribué par.*|distributed by.*" // Distributeur
        };

        for (String pattern : patternsToRemove) {
            cleanedText = cleanedText.replaceAll(pattern, "").trim();
        }

        cleanedText = cleanedText.replaceAll("\\s+", " ").trim();

        if (!cleanedText.isEmpty()) {
            // Capitalisation : première lettre en majuscule, le reste en minuscules
            cleanedText = cleanedText.substring(0, 1).toUpperCase() + cleanedText.substring(1).toLowerCase();
        }

        System.out.println("Nom nettoyé final : " + cleanedText);
        return cleanedText.isEmpty() ? "Non détecté" : cleanedText;
    }

    private String cleanLine(String line) {
        String[] patternsToRemove = {
                "(?i)(?:lot|l.n|lot\\.|n°|no|num|numéro)[\\s:]*[a-zA-Z0-9\\s]{4,}",
                "(?i)(?:exp|expiration|date exp\\.|date d'expiration|valable jusqu'à|use by|best before)[\\s:]*(\\d{2}/\\d{2,4}(?:/\\d{4})?)",
                "(?i)(?:fab|fabrication|date fab\\.|date de fabrication|fabriqué le|mfg|manufactured)[\\s:]*(\\d{2}/\\d{4}|\\d{2}/\\d{2}/\\d{4})",
                "(?i)(?:pc|code produit|code prod\\.|ean|upc)[\\s:]*\\d{12,14}",
                "(?i)ppt[\\s:]*\\d+,\\d+\\s*(dt|eur|usd)",
                "(?i)\\d+\\s*(comprimés?|capsules?|gélules?|sachets?|flacons?|tablets?|pills?)(?:\\s*effervescents?)?",
                "(?i)\\s*effervescent(e)?s?",
                "(?i)\\s*\\d+\\s*(ml|mg|g|kg)[\\s]*",
                "(?i)made in [a-zA-ZÀ-ÿ]+",
                "(?i)[a-zA-ZÀ-ÿ]+ (laboratories|laboratoire|pharma|pharmaceuticals)",
                "(?i)fl\\s*\\d+\\s*ml",
                "(?i)\\d{1,4}\\s*-\\s*\\d{1,4}\\s*(ou|uuo)?\\s*\\d{2}\\s*\\d{3}\\s*\\d{3}",
                "(?i)pharmacie.*|el raed.*|el malek.*|prix.*|pharmacy.*|drugstore.*|الصيدلية.*|الملك.*|الثمن.*",
                "(?i)\\b\\d{1,4}\\b",
                "[^a-zA-ZÀ-ÿ\\s\\d]",
                "(?i)(?:for|pour) (external|oral|intravenous) use",
                "(?i)batch.*|serial.*|s/n.*",
                "(?i)keep out of reach.*|conserver hors.*",
                "(?i)distribué par.*|distributed by.*"
        };

        String cleanedLine = line;
        for (String pattern : patternsToRemove) {
            cleanedLine = cleanedLine.replaceAll(pattern, "").trim();
        }

        cleanedLine = cleanedLine.replaceAll("\\s+", " ").trim();

        if (!cleanedLine.isEmpty()) {
            cleanedLine = cleanedLine.substring(0, 1).toUpperCase() + cleanedLine.substring(1).toLowerCase();
        }

        return cleanedLine;
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
        Matcher expMatcher = EXP_DATE_PATTERN.matcher(fullText);
        if (expMatcher.find()) {
            return expMatcher.group(1);
        }

        Matcher dateMatcher = DATE_PATTERN.matcher(fullText);
        if (dateMatcher.find()) {
            return dateMatcher.group();
        }

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
            LocalDate currentDate = LocalDate.now();

            return !expirationDate.isBefore(currentDate);
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
                if (potentialDate.matches("\\d{2}[/-]\\d{2}[/-]\\d{2,4}")) return potentialDate;
            }
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