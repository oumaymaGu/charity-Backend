package tn.example.charity.ai;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Classe responsable du prétraitement des textes pour l'analyse IA
 */
@Component
public class TextPreprocessor {

    private final Set<String> stopWords;
    private final Pattern punctuationPattern = Pattern.compile("[\\p{Punct}]");

    public TextPreprocessor() {
        // Mots vides en français
        stopWords = new HashSet<>(Arrays.asList(
                "le", "la", "les", "un", "une", "des", "et", "ou", "mais", "donc",
                "car", "pour", "par", "dans", "sur", "avec", "sans", "je", "tu", "il",
                "elle", "nous", "vous", "ils", "elles", "ce", "cette", "ces", "mon",
                "ton", "son", "ma", "ta", "sa", "mes", "tes", "ses", "notre", "votre",
                "leur", "nos", "vos", "leurs", "que", "qui", "quoi", "dont", "où",
                "quand", "comment", "pourquoi", "si", "oui", "non", "à", "au", "aux",
                "de", "du", "des", "en", "y", "est", "sont", "sera", "seront", "était",
                "étaient", "été", "être", "avoir", "a", "as", "avons", "avez", "ont",
                "avait", "avaient", "eu", "aura", "auront", "fait", "faire", "dit",
                "dire", "va", "vont", "aller", "c'est", "s'est", "comme", "plus",
                "moins", "très", "peu", "beaucoup", "trop", "pas", "ne", "n'", "se"
        ));
    }

    /**
     * Prétraite un texte en le normalisant
     * @param text Texte à prétraiter
     * @return Texte prétraité
     */
    public String preprocess(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }

        // Convertir en minuscules
        String processed = text.toLowerCase();

        // Supprimer la ponctuation
        processed = punctuationPattern.matcher(processed).replaceAll(" ");

        // Supprimer les chiffres
        processed = processed.replaceAll("\\d+", "");

        // Supprimer les espaces multiples
        processed = processed.replaceAll("\\s+", " ").trim();

        return processed;
    }

    /**
     * Tokenize un texte en mots significatifs
     * @param text Texte à tokenizer
     * @return Liste de tokens
     */
    public List<String> tokenize(String text) {
        String processed = preprocess(text);
        String[] tokens = processed.split("\\s+");

        // Filtrer les mots vides et les tokens trop courts
        return Arrays.stream(tokens)
                .filter(token -> !stopWords.contains(token) && token.length() > 1)
                .collect(Collectors.toList());
    }

    /**
     * Extrait les mots-clés d'un texte
     * @param text Texte à analyser
     * @param keywordMap Map de mots-clés avec leurs poids
     * @return Liste des mots-clés trouvés
     */
    public List<String> extractKeywords(String text, Set<String> keywordMap) {
        String processed = preprocess(text);
        return keywordMap.stream()
                .filter(keyword -> processed.contains(keyword))
                .collect(Collectors.toList());
    }
}